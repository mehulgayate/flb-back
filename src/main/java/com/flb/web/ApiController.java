package com.flb.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.xml.crypto.Data;

import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.evalua.entity.support.DataStoreManager;
import com.flb.entity.Server;
import com.flb.entity.ServerLoad;
import com.flb.entity.support.Repository;

@Controller
public class ApiController {

	@Resource
	private Repository repository;
	
	@Resource
	private DataStoreManager dataStoreManager;

	@RequestMapping("/")
	public ModelAndView showHome(HttpSession httpSession) throws InterruptedException{
		return new ModelAndView("index");
	}

	@RequestMapping("/api/simple-service")
	public ModelAndView login(HttpSession httpSession,@RequestParam Long id,@RequestParam String uuidf) throws InterruptedException{
		ModelAndView mv=new ModelAndView("json-string");

		Server server =repository.findServerById(id);
		ServerLoad  serverLoad =repository.findServerLoadByServer(server);

		JSONObject jsonObject=new JSONObject();
		if(server.isMigrationActive()){			
			if(server.getRequestCapacity()+server.getCapacityThreshold()<serverLoad.getRequestCount()){
				System.out.println("**** #### ***** Server overloaded.......");
				System.out.println("**** #### ***** **** #### ***** Migrating Load.......");
				jsonObject.put("result", "false");
				jsonObject.put("migrate", "true");
				mv.addObject("result", jsonObject);
				callNotify(id,uuidf);
				System.out.println("**** #### ***** **** #### ***** **** #### ***** Load Migrated from this server side.......");
				
				return mv;
			}
		}

		System.out.println("**************** Request recieved ...Processing.....uuid="+uuidf);

		Thread.currentThread().sleep(repository.getRequestTime());
		callNotify(id,uuidf);
		jsonObject.put("result", "true");
		mv.addObject("result", jsonObject);
		System.out.println("**************** Request processing complete.....");
		return mv;
	}

	private void callNotify(Long id,String uuid){

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
			System.out.println("Calling notify .... for uuid : "+uuid);
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
