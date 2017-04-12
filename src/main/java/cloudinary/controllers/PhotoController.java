package cloudinary.controllers;
import cloudinary.lib.PhotoUploadValidator;
import cloudinary.models.PhotoUpload;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.cloudinary.Singleton;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import java.awt.*;
import java.lang.Math.*;
import org.springframework.web.bind.annotation.*;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.FetchOptions;
import java.io.IOException;
import java.util.*;
import java.util.List;

import org.esxx.js.protocol.GAEConnectionManager;
import social.FBConnection;
import social.FBGraph;

import javax.servlet.ServletException;
import javax.servlet.http.*;

@Controller
@RequestMapping("/")

public class PhotoController extends HttpServlet {

	private final static GAEConnectionManager connectionManager = new GAEConnectionManager();
    private static final long serialVersionUID = 1L;
    private String code="";

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String login(ModelMap model) {
        return "index";//;"index"
    }


    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String service(HttpServletRequest req, HttpServletResponse res, ModelMap model)
            throws ServletException, IOException {

        code = req.getParameter("code");
        if (code == null || code.equals("")) {
//            throw new RuntimeException(
//                    "ERROR: Didn't get code parameter in callback.");
            return "index";//;"index"

        }

        FBConnection fbConnection = new FBConnection();
        String accessToken = fbConnection.getAccessToken(code);

        FBGraph fbGraph = new FBGraph(accessToken);

        String graph = fbGraph.getFBGraph();
        Map<String, String> fbProfileData = fbGraph.getGraphData(graph);

        model.addAttribute("current_user_id", fbProfileData.get("id")); // STORING CURRENT USER'S ID
        model.addAttribute("current_user_name", fbProfileData.get("name")); // STORING CURRENT USER'S NAME

        Cookie usercookie = new Cookie("userid", fbProfileData.get("id")); // http://stackoverflow.com/questions/26652679/passing-parameters-from-jsp-to-spring-controller-method
        usercookie.setMaxAge(10000); //set expire time to 10000 sec
        res.addCookie(usercookie); //put cookie in response

        Cookie accesstoken = new Cookie("accesstoken", accessToken); // http://stackoverflow.com/questions/26652679/passing-parameters-from-jsp-to-spring-controller-method
        accesstoken.setMaxAge(10000); //set expire time to 10000 sec
        res.addCookie(accesstoken); //put cookie in response

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key photoKey = KeyFactory.createKey("photos", fbProfileData.get("id")); //model.get("current_user_id").toString()
        List<Entity> photoEntities = datastore.prepare(new Query("photo", photoKey)).asList(FetchOptions.Builder.withDefaults());
        List<PhotoUpload> photos = new java.util.ArrayList<PhotoUpload>();

        for(int i = 0, n = photoEntities.size(); i < n; i++)
        {
            photos.add(new PhotoUpload(photoEntities.get(i)));
        }

        model.addAttribute("photos", photos);
        return "photos";//;"index"
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String getFBLogoutUrl(HttpServletRequest req, HttpServletResponse res) {

        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        Cookie[] cookies = req.getCookies();
        if (cookies != null)
            for (int i = 0; i < cookies.length; i++) {
                cookies[i].setValue("");
                cookies[i].setPath("/");
                cookies[i].setMaxAge(0);
                res.addCookie(cookies[i]);
            }

        return "index";
    }

    @RequestMapping(value = "/homepage", method = RequestMethod.GET)
    public String homepage(@CookieValue("userid") String userid,ModelMap model) {

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key photoKey = KeyFactory.createKey("photos", userid); //model.get("current_user_id").toString()
        List<Entity> photoEntities = datastore.prepare(new Query("photo", photoKey)).asList(FetchOptions.Builder.withDefaults());
        List<PhotoUpload> photos = new java.util.ArrayList<PhotoUpload>();


        for(int i = 0, n = photoEntities.size(); i < n; i++)
        {
            photos.add(new PhotoUpload(photoEntities.get(i)));
        }

        model.addAttribute("photos", photos);
        return "photos";//;"index"
    }

    @RequestMapping(value = "/transform", method = RequestMethod.POST)
    public String transformPhoto(@ModelAttribute("returnURL") String returnURL, @CookieValue("userid") String userid,  @RequestParam(value = "imageurl") String url, @RequestParam(value = "imageformat") String format, @RequestParam(value = "imageid") String publicid, ModelMap model) throws IOException {

        returnURL = "";
        String imageformat = format;
        String imageid = publicid;
        String finalcolor = "";

        int xCoordinate = 0;
        int yCoordinate = 0;
        double finalx = 0.;
        double finaly = 0.;

    //          BEGINNING
        Map apiresponse = null;

        try {

//              GET PICTURE COLOUR
            apiresponse = Singleton.getCloudinary().api().resource(imageid, ObjectUtils.asMap(
                    "colors", true));  // JSON ENCODING

            int width = (int) apiresponse.get("width");
            int height = (int) apiresponse.get("height");

            boolean lbreak = false;

            while (!lbreak) {
                String trans = "";

                int ltempX = xCoordinate;
                int ltempY = yCoordinate;

                if (java.lang.Math.ceil(xCoordinate + width / 10) < width) {

                    xCoordinate += java.lang.Math.floor(width / 10);
                } else if (java.lang.Math.ceil(yCoordinate + height / 10) < height) {
                    xCoordinate = 0;
                    yCoordinate += java.lang.Math.floor(height / 10);
                } else
                    break;

                if (java.lang.Math.ceil(ltempX + width / 10) < width) {
                    trans = Singleton.getCloudinary().url()
                            .transformation(
                                    new Transformation().width((int) java.lang.Math.floor(width / 10)).height((int) java.lang.Math.floor(height / 10)).x(ltempX).y(ltempY).crop("crop")
                            ).imageTag(imageid+ "." + imageformat);


                    String transURL = trans.substring(trans.indexOf("=") + 2, trans.indexOf("'", trans.indexOf("=") + 5));

                    Map options = ObjectUtils.asMap("colors", true);
                    Map tranformedupload = Singleton.getCloudinary().uploader().upload(
                            transURL, options);

                    String temp = "";
                    ArrayList finalarray = new ArrayList();

                    ArrayList arrayList = (ArrayList) tranformedupload.get("colors");
                    for (int i = 0; i < arrayList.size(); i++) {
                        temp = arrayList.get(i).toString();

                        String color = temp.substring(temp.indexOf("#"), temp.indexOf(","));
                        String percentage = temp.substring(temp.indexOf(",") + 1, temp.length() - 1);

                        if (Double.parseDouble(percentage) > 75.0) {
                            finalcolor = color;
                            finalx = ltempX + (width / 10) / 2;
                            finaly = ltempY + (height / 10) / 2;
                            lbreak = true;
                            break;
                        }

                        finalarray.add(i, color.replaceAll("\\s", ""));
                        finalarray.add(i + 1, percentage.replaceAll("\\s", ""));
                    }
                }
            }
        } catch (java.lang.Exception exception) {
        }


//      ENCODING TEXT INTO THE FOUND COORDINATES

        if (finalcolor.contains("#")) {
            int r = Integer.valueOf(finalcolor.substring(1, 3), 16);
            int g = Integer.valueOf(finalcolor.substring(3, 5), 16);
            int b = Integer.valueOf(finalcolor.substring(5, 7), 16);

            r = transformcolor(r);
            g = transformcolor(g);
            b = transformcolor(b);

            String hex = String.format("#%02x%02x%02x", r, g, b);

            Map textParams = ObjectUtils.asMap(
                    "public_id", "dark_name",
                    "font_family", "Arial",
                    "font_size", 5,
                    "font_color", hex,
                    "opacity", "100"
            );

            Map textResult = Singleton.getCloudinary().uploader().text("some text", textParams);
            String finalpicture = Singleton.getCloudinary().url().transformation(
                    new Transformation()
                            .x((int) finalx)
                            .y((int) finaly)
                            .gravity("north_west")
                            .overlay("text:dark_name:Nathan+Drake"))
                    .imageTag(imageid + "." + imageformat, ObjectUtils.emptyMap());

            returnURL = finalpicture;
        }

        model.addAttribute("returnURL", returnURL);
        return "transformed";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadPhoto(@CookieValue("userid") String userid, @ModelAttribute PhotoUpload photoUpload, BindingResult result, ModelMap model) throws IOException {

        String userID = userid;
        PhotoUploadValidator validator = new PhotoUploadValidator();
        validator.validate(photoUpload, result);

        Map uploadResult = null;
        if (photoUpload.getFile() != null && !photoUpload.getFile().isEmpty()) {
            Singleton.getCloudinary().config.properties.put("connectionManager", connectionManager);

            //INITIAL UPLOAD
            uploadResult = Singleton.getCloudinary().uploader().upload(photoUpload.getFile().getBytes(),
                    ObjectUtils.asMap("resource_type", "auto"));

            photoUpload.setPublicId((String) uploadResult.get("public_id"));
            photoUpload.setVersion(((Integer) uploadResult.get("version")).longValue());
            photoUpload.setSignature((String) uploadResult.get("signature"));
            photoUpload.setFormat((String) uploadResult.get("format"));
            photoUpload.setResourceType((String) uploadResult.get("resource_type"));

        }

//      PUTTING PICTURE INTO DATASTORE
        if (result.hasErrors())
        {
            model.addAttribute("photoUpload", photoUpload);
            return "upload_form";
        }
        else
        {
            Key photoKey = KeyFactory.createKey("photos", userID); //model.get("current_user_id").toString()
            Entity photo = new Entity("photo", photoKey);

            photoUpload.toEntity(photo);
            model.addAttribute("upload", uploadResult);
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            datastore.put(photo);

            model.addAttribute("photo", photoUpload);
            return "upload";
        }
    }

    @RequestMapping(value = "/upload_form", method = RequestMethod.GET)
    public String uploadPhotoForm(ModelMap model) {
        model.addAttribute("photo", new PhotoUpload());
        return "upload_form";
    }

    @RequestMapping(value = "/direct_upload_form", method = RequestMethod.GET)
    public String directUploadPhotoForm(ModelMap model) {
        model.addAttribute("photo", new PhotoUpload());
        return "direct_upload_form";
    }


    public int transformcolor(int color) {

        if(color > 127) {

            int lCounter = 0;
            while ((color - 5) > 0) {
                color -= 5;
                lCounter += 5;

                if (lCounter > 30)
                    break;
            }
        }
        else {
            int lCounter = 0;
            while ((color + 5) < 255) {
                color += 5;
                lCounter += 5;

                if (lCounter > 30)
                    break;
            }
        }
        return color;
    }

}
