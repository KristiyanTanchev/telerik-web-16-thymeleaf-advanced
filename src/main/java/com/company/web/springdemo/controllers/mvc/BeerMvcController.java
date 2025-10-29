package com.company.web.springdemo.controllers.mvc;

import com.company.web.springdemo.exceptions.EntityDuplicateException;
import com.company.web.springdemo.exceptions.EntityNotFoundException;
import com.company.web.springdemo.helpers.BeerMapper;
import com.company.web.springdemo.models.Beer;
import com.company.web.springdemo.models.BeerDto;
import com.company.web.springdemo.models.Style;
import com.company.web.springdemo.models.User;
import com.company.web.springdemo.services.BeerService;
import com.company.web.springdemo.services.StyleService;
import com.company.web.springdemo.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/beers")
public class BeerMvcController {

    private final BeerService beerService;
    private final UserService userService;
    private final StyleService styleService;

    private final BeerMapper beerMapper;


    @Autowired
    public BeerMvcController(BeerService beerService,
                             UserService userService,
                             StyleService styleService,
                             BeerMapper beerMapper) {
        this.beerService = beerService;
        this.userService = userService;
        this.styleService = styleService;
        this.beerMapper = beerMapper;
    }

    @ModelAttribute("styles")
    public List<Style> populateStyles() {
        return styleService.get();
    }

    @ModelAttribute("requestURI")
    public String populateRequestUri(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/{id}")
    public String showBeer(Model model, @PathVariable int id) {
        try {
            Beer beer = beerService.get(id);
            model.addAttribute("creatorEmail", beer.getCreatedBy().getEmail());
            model.addAttribute("beer", beer);
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode",
                    HttpStatus.NOT_FOUND.getReasonPhrase());
            return "ErrorView";
        }

        return "SingleBeerView";
    }

    @GetMapping
    public String showBeers(Model model,
                            @RequestParam(required = false) String beerName,
                            @RequestParam(required = false) String styleName,
                            @RequestParam(required = false) Double minAbv,
                            @RequestParam(required = false) Double maxAbv,
                            @RequestParam(defaultValue = "name") String sortBy,
                            @RequestParam(defaultValue = "asc") String sortOrder) {
        List<Beer> beers = beerService.get(beerName, minAbv, maxAbv, styleName, sortBy, sortOrder);

        model.addAttribute("beers", beers);

        return "BeersView";
    }

    @GetMapping("/new")
    public String createBeer(Model model) {
        model.addAttribute("beer", new BeerDto());
        model.addAttribute("buttonLabel", "Create");
        return "CreateBeerView";
    }

    @PostMapping("/new")
    public String createBeer(@Valid @ModelAttribute("beer") BeerDto beerDto, BindingResult errors, Model model) {
        Beer beer = null;
        model.addAttribute("buttonLabel", "Create");
        if (errors.hasErrors()) {
            return "CreateBeerView";
        }
        try {
            beer = beerMapper.fromDto(beerDto);
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }

        //TODO Get user
        User user = userService.get(1);

        try {
            beerService.create(beer, user);
            return "redirect:/beers";
        } catch (EntityDuplicateException e) {
            errors.rejectValue("name", "beer.exists", e.getMessage());
            return "CreateBeerView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/update/{id}")
    public String updateBeer(Model model, @PathVariable int id) {
        model.addAttribute("buttonLabel", "Update");
        Beer beer = beerService.get(id);
        BeerDto beerDto = new BeerDto();
        beerDto.setName(beer.getName());
        beerDto.setAbv(beer.getAbv());
        beerDto.setStyleId(beer.getStyle().getId());
        model.addAttribute("beer", beerDto);
        return "BeerUpdateView";
    }


    @PostMapping("/update/{id}")
    public String updateBeer(@Valid @ModelAttribute("beer") BeerDto beerDto, BindingResult errors, Model model,
                             @PathVariable int id) {
        model.addAttribute("buttonLabel", "Update");
        Beer beer = null;
        if (errors.hasErrors()) {
            return "BeerUpdateView";
        }
        try {
            beer = beerMapper.fromDto(id, beerDto);
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }

        //TODO Get user
        User user = userService.get(1);

        try {
            beerService.update(beer, user);
            return "redirect:/beers/" + id;
        } catch (EntityDuplicateException e) {
            errors.rejectValue("name", "beer.exists", e.getMessage());
            return "BeerUpdateView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteBeer(Model model, @PathVariable int id) {

        //TODO Get user
        User user = userService.get(1);

        try {
            beerService.delete(id, user);
            return "redirect:/beers";
        }
        catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }


}
