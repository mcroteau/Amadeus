package social.amadeus.web;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import social.amadeus.model.Sheet;
import social.amadeus.service.SheetService;

@Controller
public class SheetController {

    Gson gson = new Gson();

    @Autowired
    SheetService sheetService;

    @RequestMapping(value="/sheet/{id}", method=RequestMethod.GET)
    public @ResponseBody String getData(@PathVariable Long id){
        return gson.toJson(sheetService.getData(id));
    }

    @RequestMapping(value="/engage/{endpoint}", method=RequestMethod.GET)
    public String engage(ModelMap modelMap, @PathVariable String endpoint){
        return sheetService.engage(endpoint, modelMap);
    }

    @RequestMapping(value="/sheet/create", method=RequestMethod.GET)
    public String create(){
        return sheetService.create();
    }

    @RequestMapping(value="/sheet/save", method=RequestMethod.POST)
    public String save(RedirectAttributes redirect,
                       @ModelAttribute("sheet") Sheet sheet,
                       @RequestParam(value="sheetImage", required=false) CommonsMultipartFile sheetImage) {
        return sheetService.save(sheet, sheetImage, redirect);
    }

    @RequestMapping(value="/sheet/edit/{id}", method=RequestMethod.GET)
    public String edit(ModelMap modelMap, @PathVariable Long id){
        return sheetService.edit(id, modelMap);
    }

    @RequestMapping(value="/sheet/update", method=RequestMethod.POST)
    public String update(@ModelAttribute("sheet") Sheet sheet,
                         final RedirectAttributes redirect,
                         @RequestParam(value="sheetImage", required=true) CommonsMultipartFile sheetImage) {
        return sheetService.update(sheet, sheetImage, redirect);
    }

    @RequestMapping(value="/sheet/delete/{id}", method=RequestMethod.POST)
    public String edit(RedirectAttributes redirect,
                       @PathVariable Long id){
        return sheetService.delete(id, redirect);
    }

    @RequestMapping(value="/admin/sheet/list", method=RequestMethod.GET)
    public String getSheets(ModelMap modelMap){
        return sheetService.getSheets(modelMap);
    }

    @RequestMapping(value="/sheet/list/{id}", method=RequestMethod.GET)
    public String getUserSheets(ModelMap modelMap, @PathVariable Long id){
        return sheetService.getUserSheets(id, modelMap);
    }

}
