package org.tiaacref.ad.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.tiaacref.ad.App;
import org.tiaacref.ad.SonarOverTime;

public class Nav extends HttpServlet {

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String redirect = "index.jsp";

		try {

			String action = request.getParameter("action");
			if(!StringUtils.isBlank(action))
			{
				if(action.equals("create"))
				{
					SonarOverTime sot = new SonarOverTime();
					String projects[] = sot.getAllUniqueProjects(App.getBFConnection());
					if(null == projects)
					{
				    	Logger.getLogger(Nav.class).error("projects is null!");
					}
					else
					{
				    	Logger.getLogger(Nav.class).debug("projects is not null!");						
					}

					request.setAttribute("projects", projects);
					redirect = "/create.jsp";
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			request.getRequestDispatcher(redirect).forward(request, response);
		}

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}