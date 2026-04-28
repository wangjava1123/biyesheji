const Api = {
  checkCode: "/account/checkCode",
  login: "/account/login",
  logout: "/account/logout",
  getResource: "/api/file/getResource?filePath=",

  loadDashboard: "/dashboard/loadDashboard",

  loadSysDict: "/settings/loadSysDictList",
  saveSysDict: "/settings/saveSysDict",
  delDict: "/settings/delSysDict",
  changeDictSort: "/settings/changeSort",

  loadProduct: "/product/loadProduct",
  saveProduct: "/product/saveProduct",
  changeProductSort: "/product/changeProductSort",
  delProduct: "/product/delProduct",

  loadOrder: "/order/loadOrder",

  loadMusic: "/music/loadMusic",
  changeMusicCommendType: "/music/changeMusicCommendType",
  loadMusicCoverRecordList: "/musicCover/loadCoverRecordList",
  loadMusicCoverSummary: "/musicCover/loadCoverSummary",

  loadUser: "/user/loadUser",
  changeUserStatus: "/user/changeUserStatus",
  changeIntegral: "/user/changeIntegral",

  loadPaycodeList: "/payCode/loadPayCodeList",
  createCode: "/payCode/createCode",
  delCode: "/payCode/delCode",
};

export default Api;
