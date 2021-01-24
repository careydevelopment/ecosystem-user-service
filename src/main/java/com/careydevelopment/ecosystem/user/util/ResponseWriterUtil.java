package com.careydevelopment.ecosystem.user.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.careydevelopment.ecosystem.user.model.ResponseStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResponseWriterUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseWriterUtil.class);
	
	public static void writeResponse(HttpServletResponse response, String str) {
		try (PrintWriter writer = response.getWriter()) {
			writer.write(str);
			writer.flush();
		} catch (IOException ie) {
			LOG.error("Problem writing output to response!", ie);
		}
	}
	
	
	public static void writeErrorResponse(HttpServletResponse response, String str) {
		ResponseStatus status = new ResponseStatus();
		status.setStatusCode(ResponseStatus.StatusCode.ERROR);
		status.setMessage(str);
		
		try (PrintWriter writer = response.getWriter()) {
			String json = new ObjectMapper().writeValueAsString(status);
			
			writer.write(json);
			writer.flush();
		} catch (IOException ie) {
			LOG.error("Problem writing output to response!", ie);
		}
	}
}
