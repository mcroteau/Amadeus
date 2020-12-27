package social.amadeus.web;

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

    @Autowired
    SheetService sheetService;

//    @RequestMapping(value="/sheet/create", method=RequestMethod.GET)
//    public String create(){
//        return sheetService.create();
//    }
//
//    @RequestMapping(value="/sheet/save", method=RequestMethod.POST)
//    public String save(RedirectAttributes redirect,
//                       @ModelAttribute("sheet") Sheet sheet,
//                       @RequestParam(value="sheetImage", required=false) CommonsMultipartFile sheetImage) {
//        return sheetService.save(sheet, sheetImage, redirect);
//    }
//
//    @RequestMapping(value="/sheet/edit/{id}", method=RequestMethod.GET)
//    public String edit(ModelMap modelMap, @PathVariable Long id){
//        return sheetService.edit(id, modelMap);
//    }
//
//    @RequestMapping(value="/sheet/update", method=RequestMethod.POST)
//    public String update(@ModelAttribute("sheet") Sheet sheet,
//                         final RedirectAttributes redirect,
//                         @RequestParam(value="flyerImage", required=true) CommonsMultipartFile flyerImage) {
//        return sheetService.update(sheet, flyerImage, redirect);
//    }
//
//    @RequestMapping(value="/admin/sheet/list", method=RequestMethod.GET)
//    public String flyers(ModelMap modelMap){
//        return sheetService.getSheets(modelMap);
//    }
//
//    @RequestMapping(value="/sheet/list/{id}", method=RequestMethod.GET)
//    public String userFlyers(ModelMap modelMap, @PathVariable String id){
//        return sheetService.getUserSheets(id, modelMap);
//    }

}
