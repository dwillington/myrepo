package org.tiaacref.ad.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.tiaacref.ad.App;
import org.tiaacref.ad.SonarOverTime;

@UrlBinding("/Create.htm")
public class CreateActionBean extends BaseActionBean {

    @DefaultHandler
    public Resolution view() {
        return new ForwardResolution("/WEB-INF/jsp/create.jsp");
    }

    public String[] getAllUniqueProjects() {
		SonarOverTime sot = new SonarOverTime();
		String projects[] = sot.getAllUniqueProjects(App.getBFConnection());
        return projects;
    }

    public String getOsName() {
        return System.getProperty("os.name");
    }
}
