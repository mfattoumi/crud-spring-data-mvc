package org.mmyf.member.controller;

import com.google.gson.Gson;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import org.mmyf.member.entity.Member;
import org.mmyf.member.repository.MemberRepository;

@Controller
public class MemberController {

    private final static Logger LOGGER = Logger.getLogger(MemberController.class.getName());

    @Autowired
    private MemberRepository memberRepository;
    
     @RequestMapping(value = "/", method = RequestMethod.GET)
    public String listMembers(ModelMap model) {
        model.addAttribute("member", new Member());
        model.addAttribute("members", memberRepository.findAll());
        return "members"; 
    }
    
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addMember(@ModelAttribute("member") Member member, BindingResult result) {
        memberRepository.save(member);
        return "redirect:/";
    }

    @RequestMapping("/delete/{memberId}")
    public String deleteMember(@PathVariable("memberId") Long memberId) {
        memberRepository.delete(memberRepository.findOne(memberId));
        return "redirect:/";
    }

    /**
     * * ******************* JSON Call ****************
     */
    @RequestMapping(value = "api/members", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String addMemberJson(@RequestBody String json,HttpServletResponse response) throws JSONException {
        setHeaderResponse(response);
        JSONObject obj = new JSONObject(json);
        Member member = getObject(json, Member.class);
        if (member != null) {
            memberRepository.save(member);
            System.out.println(member.toString());
            return json;
        } else {
            return "{\"Result\": \"Ko\"}";
        }
    }

    @RequestMapping(value = "api/members/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String updateMemberJson(@RequestBody String json, HttpServletResponse response) throws JSONException {
        setHeaderResponse(response);
        JSONObject obj = new JSONObject(json);
        Member member = getObject(json, Member.class);
        if ((member != null) && (memberRepository.exists(member.getId()))) {
            memberRepository.save(member);
            System.out.println(member.toString());
            return json;
        } else {
            return "{\"Result\": \"Ko\"}";
        }
    }

    @RequestMapping(value = "/api/members/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String viewMemberJson(@PathVariable("id") long id, HttpServletResponse response) throws JSONException {
        setHeaderResponse(response);
        Member member = memberRepository.findOne(id);
        if (member != null) {
            JSONObject memberJSON = new JSONObject();
            memberJSON.put("id", member.getId());
            memberJSON.put("firstName", member.getFirstName());
            memberJSON.put("lastName", member.getLastName());
            memberJSON.put("email", member.getEmail());
            return memberJSON.toString();
        } else {
            return "{\"Result\": \"Ko\"}";
        }
    }

    @RequestMapping(value = "/api/members", method = RequestMethod.GET)
    @ResponseBody
    public String listMembersJson(ModelMap model,HttpServletResponse response) throws JSONException {
        setHeaderResponse(response);
        JSONArray memberArray = new JSONArray();
        for (Member member : memberRepository.findAll()) {
            JSONObject memberJSON = new JSONObject();
            memberJSON.put("id", member.getId());
            memberJSON.put("firstName", member.getFirstName());
            memberJSON.put("lastName", member.getLastName());
            memberJSON.put("email", member.getEmail());
            memberArray.put(memberJSON);
        }
        return memberArray.toString();
    }

    @RequestMapping(value = "api/members/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public String deleteMemberJson(@PathVariable("id") long id, HttpServletResponse response) throws JSONException {
        setHeaderResponse(response);
        Member member = memberRepository.findOne(id);
        JSONObject memberJSON = null;
        if (member != null) {
            memberJSON = new JSONObject();
            memberJSON.put("id", member.getId());
            memberJSON.put("firstName", member.getFirstName());
            memberJSON.put("lastName", member.getLastName());
            memberJSON.put("email", member.getEmail());
            memberRepository.delete(id);
            return memberJSON.toString();
        } else {
            return "{\"Result\": \"Ko\"}";
        }
    }

    /**
     * Convert a String json in to java Object. 
    **/
    public static <T> T getObject(final String jsonString, final Class<T> objectClass) {
        Gson gson = new Gson();
        T t = null;
        try {
            t = gson.fromJson(jsonString, objectClass);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "ERRORE " + e.toString());
        }
        return t;
    }

    /* to enable the CORS */
    private void setHeaderResponse(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3628800");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
    }
}
