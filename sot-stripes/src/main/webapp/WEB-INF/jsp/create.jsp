<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<s:layout-render name="/WEB-INF/jsp/layout.jsp" title="Welcome">
  <s:layout-component name="body">


    <div id="demo" class="container">
      <div class=""><h3>Create Bundle</h3></div>

      <h4 id="demo-zero-configuration">Name <input type="text" value="Unified Desktop"></input></h4>

      <div class="row-fluid">
        <div class="span5">
          <select name="from" id="multiselect" class="span12" size="8" multiple="multiple">

<c:set var="list" value="${actionBean.allUniqueProjects}" scope="page"/>
<c:forEach items="${list}" var="project" varStatus="loop">
<option value="1">${project}</option>
</c:forEach>


          </select>
        </div>

        <div class="span2">
          <button type="button" id="multiselect_rightAll" class="btn btn-block"><i class="icon-forward"></i></button>
          <button type="button" id="multiselect_rightSelected" class="btn btn-block"><i class="icon-chevron-right"></i></button>
          <button type="button" id="multiselect_leftSelected" class="btn btn-block"><i class="icon-chevron-left"></i></button>
          <button type="button" id="multiselect_leftAll" class="btn btn-block"><i class="icon-backward"></i></button>
        </div>

        <div class="span5">
          <select name="to" id="multiselect_to" class="span12" size="8" multiple="multiple"></select>
        </div>
      </div>

      <div class="span5">
        <button type="submit">Save</i></button>
      </div>


  </s:layout-component>
</s:layout-render>
