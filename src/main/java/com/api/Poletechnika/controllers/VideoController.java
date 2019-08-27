package com.api.Poletechnika.controllers;

import com.api.Poletechnika.exceptions.AccessException;
import com.api.Poletechnika.exceptions.NotFoundException;
import com.api.Poletechnika.exceptions.WrongDataException;
import com.api.Poletechnika.models.*;
import com.api.Poletechnika.repository.*;
import com.api.Poletechnika.utils.Constants;
import com.api.Poletechnika.utils.FilterUtil;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping(value = "/videos")
public class VideoController {

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    VideoFiltersRepository videoFiltersRepository;

    @Autowired
    UserPermissionRepository userPermissionRepository;
    @Autowired
    UserRepository userRepository;

    //GET ALL VIDEOS
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Iterable<Video> getVideos(@RequestParam(defaultValue = "") String name, @RequestParam(defaultValue = "") String carType,
                                     @RequestParam(defaultValue = "") String carModel, @RequestParam(defaultValue = "") String breakType) {
        List<String> enteredFilters = new ArrayList<>();
        if(carType.trim().length() > 0){
            enteredFilters.add(carType);
        }
        if(carModel.trim().length() > 0){
            enteredFilters.add(carModel);
        }
        if(breakType.trim().length() > 0){
            enteredFilters.add(breakType);
        }

        ArrayList<Video> allVideos = (ArrayList) videoRepository.findAll();

        //Check filters
        for(String filter: enteredFilters){
            Iterator<Video> it = allVideos.iterator();
            while (it.hasNext()) {
                Video video = it.next();
                if (!new FilterUtil().searchFilter(video.getFilters(), filter)) {
                    it.remove();
                }
            }
        }
        //Check name or hashtags
        if(name.trim().length() > 0){
            Iterator<Video> it = allVideos.iterator();
            while (it.hasNext()) {
                Video video = it.next();
                if (!new FilterUtil().searchNameOrHash(name, video.getTitle(), video.getHashtags())) {
                    it.remove();
                }
            }
        }

        return allVideos;
    }


    //ADD NEW VIDEO
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "", method = RequestMethod.POST)
    public Video addVideo(@RequestHeader("Authorization") String token, @RequestParam int person_id,
                                    @RequestParam String title, @RequestParam String image,
                                    @RequestParam String video, @RequestParam String description,
                                    @RequestParam String hashtags, @RequestParam String filters,
                                    @RequestParam int breakdownId) {
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null &&  token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                Video newVideo = new Video();
                if(title.trim().length() > 0 && image.trim().length() > 0 && video.trim().length() > 0 && description.trim().length() > 0
                && hashtags.trim().length() > 0 && filters.trim().length() > 0){
                    newVideo.setTitle(title);
                    newVideo.setImage(image);
                    newVideo.setVideo(video);
                    newVideo.setDescription(description);
                    newVideo.setHashtags(hashtags);
                    newVideo.setFilters(filters);
                    newVideo.setBreakdownId(breakdownId);
                    videoRepository.save(newVideo);
                    return getVideo(newVideo.getId());
                }else {
                    throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                }
            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_ACCESS_DENIED);
        }
    }



    //GET VIDEO
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Video getVideo(@PathVariable(value = "id") int id) {
        return videoRepository.findById(id);
    }


    //UPDATE VIDEO
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public Video updateVideo(@RequestHeader("Authorization") String token, @RequestParam int person_id, @PathVariable(value = "id") int id,
                                    @RequestParam(defaultValue = "") String title, @RequestParam(defaultValue = "") String image,
                                    @RequestParam(defaultValue = "") String video, @RequestParam(defaultValue = "") String description,
                                    @RequestParam(defaultValue = "") String hashtags, @RequestParam(defaultValue = "") String filters,
                                    @RequestParam(defaultValue = "9990999") int breakdownId) {
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                Video videoUpdate = videoRepository.findById(id);
                if(videoUpdate != null){
                    if(title.trim().length() > 0){
                        videoUpdate.setTitle(title);
                    }
                    if(image.trim().length() > 0){
                        videoUpdate.setImage(image);
                    }
                    if(video.trim().length() > 0){
                        videoUpdate.setVideo(video);
                    }
                    if(description.trim().length() > 0){
                        videoUpdate.setDescription(description);
                    }
                    if(hashtags.trim().length() > 0){
                        videoUpdate.setHashtags(hashtags);
                    }
                    if(filters.trim().length() > 0){
                        videoUpdate.setFilters(filters);
                    }
                    if(breakdownId != 9990999){
                        videoUpdate.setBreakdownId(breakdownId);
                    }
                    videoRepository.save(videoUpdate);
                    return videoRepository.findById(id);
                }else {
                    throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                }
            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_ACCESS_DENIED);
        }
    }

    //DELETE VIDEO
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/{id}", method =  RequestMethod.DELETE)
    public void deleteEarthRegionItem(@RequestHeader("Authorization") String token, @RequestParam int person_id,
                                      @PathVariable(value = "id") int id){
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                if(videoRepository.findById(id) != null){
                    videoRepository.deleteById(id);
                }else {
                    throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
                }
            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_ACCESS_DENIED);
        }
    }


    //FILTERS

    //Get all filters
    @RequestMapping(value = "/filters", method = RequestMethod.GET)
    public MappingJacksonValue getFilters(@RequestParam(defaultValue = "") String title) {
        SimpleBeanPropertyFilter filterJson = SimpleBeanPropertyFilter.filterOutAllExcept("id", "title", "type", "parent");
        FilterProvider filtersForJson = new SimpleFilterProvider().addFilter("SomeBeanFilter", filterJson);
        MappingJacksonValue mapping;

        Iterable<VideoFilter> filtersAll = videoFiltersRepository.findAll();
        if(title.trim().length() > 0){
            List<VideoFilter> findedFilters = new ArrayList<>();
            for(VideoFilter filterItem : filtersAll){
                if(new FilterUtil().searchName(filterItem.getTitle(), title)){
                    findedFilters.add(filterItem);
                }
            }
            mapping = new MappingJacksonValue(findedFilters);
        }else {
            mapping = new MappingJacksonValue(videoFiltersRepository.findAll());
        }

        mapping.setFilters(filtersForJson);
        return mapping;
    }

    //ADD NEW FILTER
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/filters", method = RequestMethod.POST)
    public MappingJacksonValue addFilter(@RequestHeader("Authorization") String token, @RequestParam int person_id,
                         @RequestParam String title, @RequestParam String type,
                         @RequestParam(defaultValue = "0") int parent) {
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                VideoFilter filter = new VideoFilter();
                if(title.trim().length() > 0 && type.trim().length() > 0){
                    filter.setTitle(title);
                    filter.setType(type);
                    if(parent != 0){
                        filter.setParent(parent);
                    }
                    videoFiltersRepository.save(filter);
                    return getOneFilter(filter.getId());
                }else {
                    throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                }
            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_ACCESS_DENIED);
        }
    }

    //GET CAR TYPE FILTERS
    @RequestMapping(value = "/filters/carType", method = RequestMethod.GET)
    public MappingJacksonValue getCarTypeFilters() {
        Iterable<VideoFilter> filters = videoFiltersRepository.findAllByType("car_type");
        List<VideoFilterLevelTop> globalFilters = new ArrayList<>();
        if(filters != null){
            for(VideoFilter filter : filters){
                VideoFilterLevelTop globalItemFilter = new VideoFilterLevelTop();
                globalItemFilter.setId(filter.getId());
                globalItemFilter.setTitle(filter.getTitle());
                List<VideoFilter> clildFilters = videoFiltersRepository.findAllByTypeAndParent("car_model", filter.getId());
                if(clildFilters != null){
                    globalItemFilter.setItems(clildFilters);
                }
                globalFilters.add(globalItemFilter);
            }
        }
        // Вывод в Json только id and title
        SimpleBeanPropertyFilter filterJson = SimpleBeanPropertyFilter.filterOutAllExcept("id", "title");
        FilterProvider filtersForJson = new SimpleFilterProvider().addFilter("SomeBeanFilter", filterJson);
        MappingJacksonValue mapping = new MappingJacksonValue(globalFilters);
        mapping.setFilters(filtersForJson);
        return mapping;
        //return globalFilters;
    }

    //GET BREAK TYPE FILTERS
    @RequestMapping(value = "/filters/breakType", method = RequestMethod.GET)
    public MappingJacksonValue getBreakTypeFilters() //Iterable<VideoFilter>
    {
        // Вывод в Json только id and title
        SimpleBeanPropertyFilter filterJson = SimpleBeanPropertyFilter.filterOutAllExcept("id", "title");
        FilterProvider filtersForJson = new SimpleFilterProvider().addFilter("SomeBeanFilter", filterJson);
        MappingJacksonValue mapping = new MappingJacksonValue(videoFiltersRepository.findAllByType("break_type"));
        mapping.setFilters(filtersForJson);
        return mapping;
        //return videoFiltersRepository.findAllByType("break_type");
    }

    //GET ONE FILTER
    @RequestMapping(value = "/filters/{id}", method = RequestMethod.GET)
    public MappingJacksonValue getOneFilter(@PathVariable(value = "id") int id) {
        if(videoFiltersRepository.findById(id) != null){
            // Вывод в Json только id and title
            SimpleBeanPropertyFilter filterJson = SimpleBeanPropertyFilter.filterOutAllExcept("id", "title", "type", "parent");
            FilterProvider filtersForJson = new SimpleFilterProvider().addFilter("SomeBeanFilter", filterJson);
            MappingJacksonValue mapping = new MappingJacksonValue(videoFiltersRepository.findById(id));
            mapping.setFilters(filtersForJson);
            return mapping;
        }else {
            throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
        }
        //return videoFiltersRepository.findById(id);
    }

    //UPDATE FILTER
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/filters/{id}", method = RequestMethod.POST)
    public MappingJacksonValue updateFilter(@RequestHeader("Authorization") String token, @RequestParam int person_id, @PathVariable(value = "id") int id,
                             @RequestParam(defaultValue = "") String title, @RequestParam(defaultValue = "") String type,
                             @RequestParam(defaultValue = "999999") int parent) {
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                VideoFilter videoFilter = videoFiltersRepository.findById(id);
                if(videoFilter != null){
                    if(title.trim().length() > 0){
                        videoFilter.setTitle(title);
                    }
                    if(type.trim().length() > 0){
                        videoFilter.setType(type);
                    }
                    if(parent != 999999  && parent != videoFilter.getId()){  //ВТОРОЕ УСЛОВИЕ - ЧТО БЫ НЕ СТАВИТЬ РОДИТЕЛЕМ САМОГО СЕБЯ
                        videoFilter.setParent(parent);
                    }
                    videoFiltersRepository.save(videoFilter);
                    return getOneFilter(id);
                }else {
                    throw new WrongDataException(Constants.ERROR_WRONG_DATA);
                }
            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_ACCESS_DENIED);
        }
    }

    //DELETE FILTER
    //REQUEST FOR ADMIN-PANEL
    //ADMINISTRATION PERMISSION
    @RequestMapping(value = "/filters/{id}", method =  RequestMethod.DELETE)
    public void deleteFilter(@RequestHeader("Authorization") String token, @RequestParam int person_id,
                                      @PathVariable(value = "id") int id){
        //CHECK ADMINISTRATION PERMISSION
        UserPermission userPermission = userPermissionRepository.findByUserId(person_id);
        if(userPermission != null && userPermission.getType().equals(Constants.ADMINISTRATION_PERMISSION_KEY)){
            if(userRepository.findById(person_id).getToken() != null && token.trim().length() > 0 && userRepository.findById(person_id).getToken().equals(token)){
                if(videoFiltersRepository.findById(id) != null){
                    videoFiltersRepository.deleteById(id);
                }else {
                    throw new NotFoundException(Constants.ERROR_DATA_NOT_FOUND);
                }
            }else {
                throw new AccessException(Constants.ERROR_WRONG_AUTH_TOKEN);
            }
        }else {
            throw new NotFoundException(Constants.ERROR_ACCESS_DENIED);
        }
    }

}
