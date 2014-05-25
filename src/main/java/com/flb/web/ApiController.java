package com.flb.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.flb.entity.Server;
import com.flb.entity.ServerLoad;
import com.flb.entity.support.Repository;

@Controller
public class ApiController {

	@Resource
	private Repository repository; 
	
	@RequestMapping("/")
	public ModelAndView showHome(HttpSession httpSession) throws InterruptedException{
		return new ModelAndView("index");
	}

	@RequestMapping("/api/simple-service")
	public ModelAndView login(HttpSession httpSession,@RequestParam Long id) throws InterruptedException{
		ModelAndView mv=new ModelAndView("json-string");
		System.out.println("**************** Request recieved ...Processing.....");
		JSONObject jsonObject=new JSONObject();
		Thread.currentThread().sleep(15000);
		callNotify(id);
		jsonObject.put("result", "true");
		mv.addObject("result", jsonObject);
		System.out.println("**************** Request processing complete.....");
		return mv;
	}

	private void callNotify(Long id){

		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet getRequest = new HttpGet(
					"http://localhost:8080/notify/request-complete?id="+id);
			getRequest.addHeader("accept", "application/json");

			HttpResponse response = httpClient.execute(getRequest);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}

			BufferedReader br = new BufferedReader(
					new InputStreamReader((response.getEntity().getContent())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}

			httpClient.getConnectionManager().shutdown();

		} catch (ClientProtocolException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

}
