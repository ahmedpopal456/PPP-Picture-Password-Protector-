package cloudinary.controllers;

import cloudinary.lib.PhotoUploadValidator;
import cloudinary.models.PhotoUpload;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.cloudinary.Singleton;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.FetchOptions;

import java.io.IOException;
import java.util.Map;
import java.util.List;

import org.esxx.js.protocol.GAEConnectionManager;
import social.FBConnection;
import social.FBGraph;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/")

// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
// SO, MY ASSUMPTION RIGHT NOW IS THAT WHATEVER IS INSIDE THE MODEL OBJECT, CAN ONLY HAVE A UNIQUE KEY
// MOREOVER, THROUGH THIS KEY, THE OBJECTS CAN BE ACCESSED FROM ONE END TO THE OTHER (VIEW TO CONTROLLER)

// ANOTHER ASSUMPTION IS THAT THE RETURN TYPE OF THESE FUNCTIONS (INSIDE THE CONTROLLER), ARE USED
// TO CONTROL THE FLOW OF THE VIEWS... THEREFORE, WE CAN JUMP FROM ONE PAGE TO THE OTHER USING THOSE

// FINAL ASSUMPTION IS THAT IN ORDER TO HAVE A DYNAMIC PAGE, WE WOULD NEED TO INTEGRATE JAVASCRIPT WITHIN
// THE APPLICATION TO CONTROL CERTAIN ASPECTS (BUTTONS, TOGGLES, HIDE/SHOW LISTS, UPDATES, ETC)...
// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


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
            throw new RuntimeException(
                    "ERROR: Didn't get code parameter in callback.");
        }

        FBConnection fbConnection = new FBConnection();
        String accessToken = fbConnection.getAccessToken(code);

        FBGraph fbGraph = new FBGraph(accessToken);

        String graph = fbGraph.getFBGraph();
        Map<String, String> fbProfileData = fbGraph.getGraphData(graph);

        model.addAttribute("current_user_id", fbProfileData.get("id")); // STORING CURRENT USER'S ID
        model.addAttribute("current_user_name", fbProfileData.get("name")); // STORING CURRENT USER'S NAME

        Cookie foo = new Cookie("userid", fbProfileData.get("id")); // http://stackoverflow.com/questions/26652679/passing-parameters-from-jsp-to-spring-controller-method
        foo.setMaxAge(10000); //set expire time to 10000 sec
        res.addCookie(foo); //put cookie in response

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

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadPhoto(@CookieValue("userid") String userid, @ModelAttribute PhotoUpload photoUpload, BindingResult result, ModelMap model) throws IOException {

        String userID = userid;

        PhotoUploadValidator validator = new PhotoUploadValidator();
        validator.validate(photoUpload, result);

        Map uploadResult = null;
        if (photoUpload.getFile() != null && !photoUpload.getFile().isEmpty()) {            
            Singleton.getCloudinary().config.properties.put("connectionManager", connectionManager);
            uploadResult = Singleton.getCloudinary().uploader().upload(photoUpload.getFile().getBytes(),
                    ObjectUtils.asMap("resource_type", "auto"));
            
            photoUpload.setPublicId((String) uploadResult.get("public_id"));
            photoUpload.setVersion(((Integer) uploadResult.get("version")).longValue());
            photoUpload.setSignature((String) uploadResult.get("signature"));
            photoUpload.setFormat((String) uploadResult.get("format"));
            photoUpload.setResourceType((String) uploadResult.get("resource_type"));
        }

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
}
