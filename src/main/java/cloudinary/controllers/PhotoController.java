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
    private String code = "";

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

        Cookie usernamecookie = new Cookie("username", fbProfileData.get("name")); // http://stackoverflow.com/questions/26652679/passing-parameters-from-jsp-to-spring-controller-method
        usernamecookie.setMaxAge(10000); //set expire time to 10000 sec
        res.addCookie(usernamecookie); //put cookie in response

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

        return "redirect:" + "http://localhost:8080/homepage"; //;//;"index"
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
    public String homepage(@CookieValue("userid") String userid, @CookieValue("username") String username, ModelMap model) {

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key photoKey = KeyFactory.createKey("photos", userid); //model.get("current_user_id").toString()
        List<Entity> photoEntities = datastore.prepare(new Query("photo", photoKey)).asList(FetchOptions.Builder.withDefaults());
        List<PhotoUpload> photos = new java.util.ArrayList<PhotoUpload>();


        for(int i = 0, n = photoEntities.size(); i < n; i++)
        {
            photos.add(new PhotoUpload(photoEntities.get(i)));
        }

        model.addAttribute("photos", photos);
        model.addAttribute("current_user_id", userid); // STORING CURRENT USER'S ID
        model.addAttribute("current_user_name", username); // STORING CURRENT USER'S NA

        return "photos";//;"index"
    }

    @RequestMapping(value = "/transform", method = RequestMethod.POST)
    public String transformPhoto(@ModelAttribute("returnURL") String returnURL, @CookieValue("userid") String userid,  @RequestParam(value = "imageurl") String url, @RequestParam(value = "imageformat") String format, @RequestParam(value = "password_input1") String password1, @RequestParam(value = "password_input2") String password2,@RequestParam(value = "password_input3") String password3 ,@RequestParam(value = "imageid") String publicid, ModelMap model) throws IOException {

        returnURL = "";
        int xCoordinate = 0;
        int yCoordinate = 0;
        int lcounter = 0; // WILL KEEP TRACK OF HOW MANY COORDINATES WERE FOUND
        int lcountermax = 0;
        double[] final_x = {0.0,0.0,0.0};
        double[] final_y = {0.0,0.0,0.0};
        String[] final_color = {"","",""};

        if(!password1.equals(""))
            lcountermax++;
        if(!password2.equals(""))
            lcountermax++;
        if(!password3.equals(""))
            lcountermax++;

        if((lcountermax == 0) || format.equals("") || publicid.equals(""))
            return "redirect:" + "http://localhost:8080/homepage";

        // BEGINNING
        Map apiresponse = null;

        try {

            // GET PICTURE COLOUR
            apiresponse = Singleton.getCloudinary().api().resource(publicid, ObjectUtils.asMap(
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
                            ).imageTag(publicid+ "." + format);


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

                            if (lcounter == 0)
                            {
                                final_color[0] = color;
                                final_x[0] = ltempX + (width / 10) / 2;
                                final_y[0] = ltempY + (height / 10) / 2;
                                lcounter++;
                            }
                            else if(lcounter == 1)
                            {
                                final_color[1] = color;
                                final_x[1] = ltempX + (width / 10) / 2;
                                final_y[1] = ltempY + (height / 10) / 2;
                                lcounter++;

                            }
                            else if(lcounter == 2)
                            {
                                final_color[2] = color;
                                final_x[2] = ltempX + (width / 10) / 2;
                                final_y[2] = ltempY + (height / 10) / 2;
                                lcounter++;
                            }

                            if(lcounter >= lcountermax)
                            {
                                lbreak = true;
                            }

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

        int lNumberofTransforms = 0;

        if(final_color[0].contains("#"))
            lNumberofTransforms++;
        if(final_color[1].contains("#"))
            lNumberofTransforms++;
        if(final_color[2].contains("#"))
            lNumberofTransforms++;


        if (lNumberofTransforms == 1)
            {
                String firstpass ="";

                if (!password1.equals("")){
                    firstpass = password1;
                }
                else if(!password2.equals(""))
                {
                    firstpass = password2;
                }
                else if(!password3.equals(""))
                {
                    firstpass = password3;
                }

                int r1 = Integer.valueOf(final_color[0].substring(1, 3), 16);
                int g1 = Integer.valueOf(final_color[0].substring(3, 5), 16);
                int b1 = Integer.valueOf(final_color[0].substring(5, 7), 16);

                r1 = transformcolor(r1);
                g1 = transformcolor(g1);
                b1 = transformcolor(b1);

                String hex = String.format("#%02x%02x%02x", r1, g1, b1);

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
                                .x((int) final_x[0])
                                .y((int) final_y[0])
                                .gravity("north_west")
                                .overlay("text:dark_name:" + firstpass))
                        .imageTag(publicid + "." + format, ObjectUtils.emptyMap());

                returnURL = finalpicture;
        }
        else if(lNumberofTransforms == 2)
        {
            String firstpass ="";
            String secondpass ="";

            if (!password1.equals(""))
            {
                firstpass = password1;
            }
            if(!password2.equals(""))
            {
                if(firstpass.equals(""))
                    firstpass = password2;
                else
                    secondpass = password2;
            }
            if(!password3.equals(""))
            {
                if(secondpass.equals(""))
                    secondpass = password3;
            }

            if(!password3.equals(""))
            {
                firstpass = password3;
            }
            int r1 = Integer.valueOf(final_color[0].substring(1, 3), 16);
            int g1 = Integer.valueOf(final_color[0].substring(3, 5), 16);
            int b1 = Integer.valueOf(final_color[0].substring(5, 7), 16);

            r1 = transformcolor(r1);
            g1 = transformcolor(g1);
            b1 = transformcolor(b1);

            String hex1 = String.format("#%02x%02x%02x", r1, g1, b1);

            int r2 = Integer.valueOf(final_color[1].substring(1, 3), 16);
            int g2 = Integer.valueOf(final_color[1].substring(3, 5), 16);
            int b2 = Integer.valueOf(final_color[1].substring(5, 7), 16);

            r2 = transformcolor(r2);
            g2 = transformcolor(g2);
            b2 = transformcolor(b2);

            String hex2 = String.format("#%02x%02x%02x", r2, g2, b2);

            Map textParams1 = ObjectUtils.asMap(
                    "public_id", "dark_name1",
                    "font_family", "Arial",
                    "font_size", 5,
                    "font_color", hex1,
                    "opacity", "100"
            );
            Map textParams2 = ObjectUtils.asMap(
                    "public_id", "dark_name2",
                    "font_family", "Arial",
                    "font_size", 5,
                    "font_color", hex2,
                    "opacity", "100"
            );
            Map textResult1 = Singleton.getCloudinary().uploader().text("some text", textParams1);
            Map textResult2 = Singleton.getCloudinary().uploader().text("some text", textParams2);

            String finalpicture = Singleton.getCloudinary().url().transformation(
                    new Transformation()
                            .overlay("text:dark_name1:" + firstpass)
                            .x((int) final_x[0])
                            .y((int) final_y[0])
                            .gravity("north_west")
                                .chain()
                            .x((int) final_x[1])
                            .y((int) final_y[1])
                            .gravity("north_west")
                            .overlay("text:dark_name2:" + secondpass))
                    .imageTag(publicid + "." + format, ObjectUtils.emptyMap());

            returnURL = finalpicture;
        }
        else if(lNumberofTransforms == 3)
        {
            int r1 = Integer.valueOf(final_color[0].substring(1, 3), 16);
            int g1 = Integer.valueOf(final_color[0].substring(3, 5), 16);
            int b1 = Integer.valueOf(final_color[0].substring(5, 7), 16);

            r1 = transformcolor(r1);
            g1 = transformcolor(g1);
            b1 = transformcolor(b1);

            String hex1 = String.format("#%02x%02x%02x", r1, g1, b1);

            int r2 = Integer.valueOf(final_color[1].substring(1, 3), 16);
            int g2 = Integer.valueOf(final_color[1].substring(3, 5), 16);
            int b2 = Integer.valueOf(final_color[1].substring(5, 7), 16);

            r2 = transformcolor(r2);
            g2 = transformcolor(g2);
            b2 = transformcolor(b2);

            String hex2 = String.format("#%02x%02x%02x", r2, g2, b2);

            int r3 = Integer.valueOf(final_color[2].substring(1, 3), 16);
            int g3 = Integer.valueOf(final_color[2].substring(3, 5), 16);
            int b3 = Integer.valueOf(final_color[2].substring(5, 7), 16);

            r3 = transformcolor(r3);
            g3 = transformcolor(g3);
            b3 = transformcolor(b3);

            String hex3 = String.format("#%02x%02x%02x", r3, g3, b3);
            Map textParams1 = ObjectUtils.asMap(
                    "public_id", "dark_name1",
                    "font_family", "Arial",
                    "font_size", 5,
                    "font_color", hex1,
                    "opacity", "100"
            );
            Map textParams2 = ObjectUtils.asMap(
                    "public_id", "dark_name2",
                    "font_family", "Arial",
                    "font_size", 5,
                    "font_color", hex2,
                    "opacity", "100"
            );
            Map textParams3 = ObjectUtils.asMap(
                    "public_id", "dark_name3",
                    "font_family", "Arial",
                    "font_size", 5,
                    "font_color", hex3,
                    "opacity", "100"
            );

            Map textResult1 = Singleton.getCloudinary().uploader().text("some text", textParams1);
            Map textResult2 = Singleton.getCloudinary().uploader().text("some text", textParams2);
            Map textResult3 = Singleton.getCloudinary().uploader().text("some text", textParams3);

            String finalpicture = Singleton.getCloudinary().url().transformation(
                    new Transformation()
                            .overlay("text:dark_name1:" + password1)
                            .x((int) final_x[0])
                            .y((int) final_y[0])
                            .gravity("north_west")
                                .chain()
                            .x((int) final_x[1])
                            .y((int) final_y[1])
                            .gravity("north_west")
                            .overlay("text:dark_name2:" + password2)
                                .chain()
                            .x((int) final_x[2])
                            .y((int) final_y[2])
                            .gravity("north_west")
                            .overlay("text:dark_name3:" + password3))
                    .imageTag(publicid + "." + format, ObjectUtils.emptyMap());

            returnURL = finalpicture;
        }
        else
        {
            returnURL = "<a>Could Not Transform Image</a>";
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
