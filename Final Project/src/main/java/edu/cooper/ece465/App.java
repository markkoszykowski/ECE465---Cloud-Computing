package edu.cooper.ece465;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.io.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.utils.IOUtils;
import spark.ResponseTransformer;
import static spark.Spark.*;

import edu.cooper.ece465.FFT.Image;

public class App {
    private static int numWorkers;
    private static final ArrayList<ObjectInputStream> ois = new ArrayList<>();
    private static final ArrayList<ObjectOutputStream> oos = new ArrayList<>();
    private static final List<String> usedPhotoID = new ArrayList<>();
    private static final Logger LOG = LogManager.getLogger(App.class);
    private static final String password = "123456";

    public static void main(String[] args) {
        try {
            LOG.debug("Worker on Private IP: " + InetAddress.getLocalHost().getHostAddress() + "started.");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        }

        Path ipFile = Paths.get("./ips.txt");

        try {
            numWorkers = (int) (Files.lines(ipFile).count() - 1);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        LOG.debug("Backend discovered " + numWorkers + " workers");
        System.out.println("Backend discovered " + numWorkers + " workers");

        for (int i = 0; i < numWorkers; i++) {
            try (ServerSocket serverSocket = new ServerSocket(6969)) {
                Socket clientSocket = serverSocket.accept();

                ois.add(new ObjectInputStream(clientSocket.getInputStream()));
                oos.add(new ObjectOutputStream(clientSocket.getOutputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File f = new File(".\\tmp");
        f.mkdir();

        staticFiles.location("/static");

        Gson gson = new GsonBuilder().setLenient().create();

        ResponseTransformer responseTransformer = model -> {
            if (model == null) {
                return "";
            }
            return gson.toJson(model);
        };

        initExceptionHandler(Throwable::printStackTrace);

        options(
                "/*",
                (request, response) -> {
                    String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
                    if (accessControlRequestHeaders != null) {
                        response.header("Access-Control-Allow-Headers", "*");
                    }

                    String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
                    if (accessControlRequestMethod != null) {
                        response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
                        response.header("Access-Control-Allow-Methods", "*");
                    }

                    return "OK";
                });

        before(
                (req, res) -> {
                    res.header("Access-Control-Allow-Origin", "*");
                    res.header("Access-Control-Allow-Headers", "*");
                    res.header("Access-Control-Expose-Headers", "*");
                    //res.type("application/json");
                    res.type("text/html");
                });

        // ping method to text connection
        get("/ping", (req, res) -> "OK");

        // upload path
        post("/upload", (req, res) -> {
            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("./tmp"));
            Part filePart = req.raw().getPart("image");

            try (InputStream inputStream = filePart.getInputStream()) {
                OutputStream outputStream = new FileOutputStream("./tmp/" + filePart.getSubmittedFileName());
                IOUtils.copy(inputStream, outputStream);
                outputStream.close();
            }
            catch (IOException e) {
                e.printStackTrace();
                res.status(500);
                return "Some error occured in file transfer";
            }

            String fileEx;
            try {
                fileEx = "." + filePart.getSubmittedFileName().split("\\.")[1];
            }
            catch (Exception e) {
                e.printStackTrace();
                res.status(415);
                return "File has no extension, must be image file.";
            }

            File orgFile = new File("./tmp/" + filePart.getSubmittedFileName());

            String fileType = new MimetypesFileTypeMap().getContentType(orgFile).split("/")[0];
            if (!fileType.equals("image")) {
                orgFile.delete();
                res.status(415);
                return "File is not a registered image file type";
            }

            String photoID = assignID(fileEx);

            // some_FFT_function(renamedFile);
            try {
                compress_image("./tmp/" + filePart.getSubmittedFileName(), "./tmp/" + photoID);
            } catch (Exception e) {
                if (e instanceof IOException) {
                    res.status(500);
                    return "File was lost on server";
                }
                else {
                    res.status(415);
                    return "File must have dimensions that are a power of 2";
                }
            }

            res.status(200);
            res.redirect("/" + photoID);
            return "File uploaded and saved.\nFile ID: " + photoID;
        });

        // download path
        get("/:photoID", (req, res) -> {
            String photoID = req.params(":photoID");
            if (!usedPhotoID.contains(photoID)) {
                res.status(404);
                return null;
            }
            res.header("Content-disposition", "attachment; filename=" + photoID);
            File file = new File("./tmp/" + photoID);
            OutputStream outputStream = res.raw().getOutputStream();
            outputStream.write(Files.readAllBytes(file.toPath()));
            outputStream.flush();

            file.delete();
            usedPhotoID.remove(photoID);
            res.status(200);
            return "File Downloaded";
        });

        // tremination path (requires password)
        post("/terminate", (req, res) -> {
            String provided = req.queryParams("password");
            if (provided.equals(password)) {
                for (int i = 0; i < numWorkers; i++) {
                    oos.get(i).writeObject("TERMINATE");
                }
                LOG.debug("Terminating backend node");
                System.exit(0);
                return null;
            }
            else {
                res.status(400);
                return "You do not have access to this.";
            }
        });
    }

    private static String assignID(String fileEx) {
        String photoID = UUID.randomUUID().toString().replace("-", "") + fileEx;
        while (usedPhotoID.contains(photoID)) {
            photoID  = UUID.randomUUID().toString().replace("-", "") + fileEx;
        }
        usedPhotoID.add(photoID);
        return photoID;
    }

    private static void compress_image(String orgPath, String comPath) throws Exception {
        Image image = new Image();
        image.readImage(orgPath);
        image.distRGBCompress(ois, oos, (float) 0.01, numWorkers);
        image.writeImage(comPath);
    }
}