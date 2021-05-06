package edu.cooper.ece465;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.utils.IOUtils;
import spark.ResponseTransformer;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static spark.Spark.*;

public class App {

    private static final List<String> usedPhotoID = new ArrayList<>();

    public static void main(String[] args) {
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

        get("/ping", (req, res) -> "OK");

        get("/:photoID", (req, res) -> {
            String photoID = req.params(":photoID");
            if (!usedPhotoID.contains(photoID)) {
                res.status(404);
                return null;
            }
            res.header("Content-disposition", "attachment; filename=" + photoID);
            File file = new File("C:/tmp/" + photoID);
            OutputStream outputStream = res.raw().getOutputStream();
            outputStream.write(Files.readAllBytes(file.toPath()));
            outputStream.flush();

            file.delete();
            usedPhotoID.remove(photoID);
            res.status(200);
            return res;
        });

        post("/upload", (req, res) -> {
            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("C:/tmp"));
            Part filePart = req.raw().getPart("image");

            try (InputStream inputStream = filePart.getInputStream()) {
                OutputStream outputStream = new FileOutputStream("C:/tmp/" + filePart.getSubmittedFileName());
                IOUtils.copy(inputStream, outputStream);
                outputStream.close();
            }
            catch (Exception e) {
                System.out.println(e);
                res.status(500);
                return "Some error occured in file transfer";
            }

            String fileEx;
            try {
                fileEx = "." + filePart.getSubmittedFileName().split("\\.")[1];
            }
            catch (Exception e) {
                System.out.println(e);
                res.status(415);
                return "File has no extension, must be image file.";
            }

            File orgFile = new File("C:/tmp/" + filePart.getSubmittedFileName());

            String fileType = new MimetypesFileTypeMap().getContentType(orgFile).split("/")[0];
            if (!fileType.equals("image")) {
                orgFile.delete();
                res.status(415);
                return "File is not a registered image file type";
            }

            String photoID = assignID(fileEx);
            File renamedFile = new File("C:/tmp/" + photoID);
            if(!orgFile.renameTo(renamedFile)) {
                orgFile.delete();
                usedPhotoID.remove(photoID);
                res.status(500);
                return "Failed to rename file.";
            }

            // some_FFT_function(renamedFile);

            res.status(200);
            return "File uploaded and saved.\nFile ID: " + photoID;
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
}