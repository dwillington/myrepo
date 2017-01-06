myrepo
======
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Hybris
- local.base.properties
  db.url=jdbc:mysql://epic1-mysql/hybrisdbuser?useConfigs=maxPerformance&characterEncoding=utf8
--------------------------------------------------------------------------------
Apache
- apache_setup_on_docker.sh: replace below with epic1-solr
  sed -i 's/${aem.publish.host}/solr-host/g' /etc/httpd/conf/dispatcher-render-qa-docker.inc
--------------------------------------------------------------------------------


