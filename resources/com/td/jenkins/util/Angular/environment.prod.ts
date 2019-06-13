import { environment as tpl } from './environment.template';

export const environment = Object.assign({}, tpl, {
  name: 'production',
  production: true,
  state: {
    redirect: true
  },
  salesforce: {
    transport: 'visualForceRemoting',
    endpoint: 'https://devfdn-preapproval-homeowner.cs20.force.com/HOJ_FullApplication_Dev'
  },
  jsForceExt: 'HOJ_State'
});

