export const environment = {
  name: 'template',
  production: false,
  deployUrl: '/resource/HOJ_FullApp/',
  baseHref: '/fullapp/',
  state: {
    redirect: true
  },
  routing: {
    guard: {
      enable: true
    }
  },
  error: {
    logging: {
      enable: true
    }
  },
  google: {
    maps: {
      apiKey: 'AIzaSyCV1AYVnkrZyRscqdAsjeA3VXR-SFWrCLo',
      clientId: 'gme-torontodominioncanada'
    }
  },
  threatMetrix: {
    url: 'https://tmx.td.com/fp/tags.js',
    orgId: '6pd9vkgk'
  },
  salesforce: {
    transport: 'localMock',
    endpoint: 'https://devfdn-preapproval-homeowner.cs20.force.com/HOJ_FullApplication_Dev',
    params: {}
  },
  jsForce: {
    deploy: {
      loginUrl: 'https://test.salesforce.com',
      api_version: 36.0,
      timeout: 120000,
      poll_interval: 5000
    },
    visualforce: {
      template: 'index.page.html',
      page: 'HOJ_FullApp',
      controller: 'HOJ_FullAppController'
    },
    resources: {
      app_resource_name: 'HOJ_FullApp',
      node_module_resource_name: 'NodeModulesFullApp'
    },
    options: {}
  },
  jsForceExt: 'HOJ_State'
};
