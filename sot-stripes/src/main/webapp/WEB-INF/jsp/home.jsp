<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<s:layout-render name="/WEB-INF/jsp/layout.jsp" title="Welcome">
  <s:layout-component name="body">

      <div class="container">
		<p>Bundle Control
			<div class="clearfix">
				<ul class="span4">
					<li><a href="view.jsp">View Bundles</a></li>
					<li><a href="/sot-stripes/Create.htm">Create Bundle</a></li>
					<li><a href="edit.jsp">Edit Bundle</a></li>
					<li><a href="delete.jsp">Delete Bundle</a></li>
				</ul>
			</div>
		</p>
      </div>

  </s:layout-component>
</s:layout-render>
