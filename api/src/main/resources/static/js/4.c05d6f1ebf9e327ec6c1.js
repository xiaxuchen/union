webpackJsonp([4],{"0LCs":function(e,t){},"4PFM":function(e,t){},ZhQ9:function(e,t,s){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var a=s("fZjL"),i=s.n(a),l=s("mvHQ"),n=s.n(l),r={props:{material:{type:Object,required:!0}}},c={render:function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("div",{staticClass:"material-item"},[s("div",{staticClass:"padding-md font-sm"},[e._v("更新于 "+e._s(e.material.updateTime))]),e._v(" "),s("div",{staticClass:"divider"}),e._v(" "),s("el-image",{staticClass:"image",attrs:{src:e.material.pic,fit:"fill"}}),e._v(" "),s("div",{staticClass:"title"},[e._v(e._s(e.material.title))]),e._v(" "),s("div",{staticClass:"cover full clearfix",on:{click:function(t){return e.$emit("select")}}},[s("b",{staticClass:"el-icon-check"})])],1)},staticRenderFns:[]};var o={mounted:function(){var e=this;this.$bus.$on("show-material-dialog",function(){e.visible=!0,e.loadMaterialList()})},data:function(){return{curPage:0,pageSize:10,visible:!1,loading:!1,materialList:[]}},components:{MaterialItem:s("VU/8")(r,c,!1,function(e){s("iq8Z")},"data-v-27ff29a5",null).exports},methods:{close:function(){this.visible=!1},setMaterialID:function(e){this.close(),this.$emit("on-check-material",e)},loadMaterialList:function(){var e=this;this.loading=!0,this.api.push.getMaterialList(this.curPage,this.pageSize).then(this.api.commonResp(function(t,s){t?(e.materialList=s,console.log(n()(s))):e.$message.error(s),e.loading=!1}))}}},u={render:function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("div",{staticClass:"material-dialog"},[s("el-dialog",{attrs:{visible:e.visible,title:"选择素材",width:"60%"},on:{close:e.close}},[s("div",{directives:[{name:"loading",rawName:"v-loading",value:e.loading,expression:"loading"}],staticClass:"item-list"},e._l(e.materialList,function(t){return s("material-item",{key:t.id,staticClass:"fl",attrs:{material:t},on:{select:function(s){return e.setMaterialID(t.id)}}})}),1)])],1)},staticRenderFns:[]};var d=s("VU/8")(o,u,!1,function(e){s("0LCs")},"data-v-3f6bc88e",null).exports,h=s("XF1/"),p={data:function(){return{activeMessage:"文本消息",textMessage:"",picTextId:null,previewLoading:!1,url:null,qrCodeVisible:!1,hintTxt:"",curIntervalId:null}},methods:{onPreviewPush:function(){var e=this,t=this.verify();t&&(this.previewLoading=!0,this.api.push.previewPush(null,t.type,t.content).then(this.api.commonResp(function(t,s){clearInterval(e.curIntervalId),e.curIntervalId=setInterval(function(){e.api.push.getPreviewState(s.pushItemId).then(e.api.commonResp(function(t,s){t&&(1===s?(e.hintTxt="发送成功",clearInterval(e.curIntervalId)):-1===s&&(e.hintTxt="预览已过期",clearInterval(e.curIntervalId)))}))},2e3),e.url=s.url,e.qrCodeVisible=!0,e.hintTxt=""},this)).finally(function(){e.previewLoading=!1}))},checkMaterial:function(e){console.log(e),this.picTextId=e},showMaterialDialog:function(){this.$bus.$emit("show-material-dialog")},sendPush:function(){var e=this.verify();e&&this.$emit("send-push",e)},verify:function(){var e=1,t=null;switch(this.activeMessage){case"文本消息":e=0,t=this.textMessage;break;case"图文消息":e=1,t=this.picTextId}return null==t||""===t.trim()?(this.$message.error("推送内容不能为空"),null):{type:e,content:t}}},components:{MaterialDialog:d,BottomButtonDialog:h.a}},m={render:function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("div",{staticClass:"pushInfo"},[s("h3",[e._v("群发内容")]),e._v(" "),s("p",{staticClass:"text-info font-sm"},[e._v("为保障用户体验，微信公众平台严禁恶意营销以及诱导分享朋友圈，严禁发布色情低俗、暴力血腥、政治谣言等各类违反法律法规及相关政策规定的信息。 ")]),e._v(" "),s("el-card",[s("el-tabs",{model:{value:e.activeMessage,callback:function(t){e.activeMessage=t},expression:"activeMessage"}},[s("el-tab-pane",{staticClass:"tab-panel",attrs:{name:"文本消息"}},[s("template",{slot:"label"},[s("b",{staticClass:"el-icon-document"}),e._v(" 文本消息\n        ")]),e._v(" "),s("el-input",{staticClass:"text-message-input",attrs:{type:"textarea",size:"medium ",resize:"none",rows:6},model:{value:e.textMessage,callback:function(t){e.textMessage=t},expression:"textMessage"}})],2),e._v(" "),s("el-tab-pane",{staticClass:"tab-panel",attrs:{name:"图文消息"}},[s("template",{slot:"label"},[s("b",{staticClass:"el-icon-picture"}),e._v(" 图文消息\n        ")]),e._v(" "),s("div",{staticClass:"full pic-text-material",on:{click:e.showMaterialDialog}},[s("div",{staticClass:"text-center"},[s("div",{staticClass:"el-icon-folder",staticStyle:{"font-size":"50px"}}),e._v(" "),s("div",{staticClass:"font-sm"},[e._v("从素材库中选取")]),e._v(" "),null!=e.picTextId?s("div",{staticClass:"font-sm"},[e._v("已选择")]):e._e()])])],2)],1),e._v(" "),s("el-col",{staticStyle:{padding:"20px"},attrs:{align:"right"}},[s("el-button",{attrs:{loading:e.previewLoading},on:{click:e.onPreviewPush}},[e._v("发送预览 "),s("b",{staticClass:"el-icon-view"})]),e._v(" "),s("el-button",{attrs:{type:"primary"},on:{click:e.sendPush}},[e._v("发送 "),s("b",{staticClass:"el-icon-s-promotion"})])],1)],1),e._v(" "),s("bottom-button-dialog",{attrs:{"bottom-visible":!1,visible:e.qrCodeVisible,width:"20"},on:{"update:visible":function(t){e.qrCodeVisible=t}}},[s("div",[s("el-row",[s("el-col",{attrs:{align:"center"}},[s("el-image",{staticStyle:{width:"200px",height:"200px"},attrs:{src:e.url,fit:"cover"}}),e._v(" "),s("div",{staticClass:"text-center"},[e._v("请使用微信扫码")]),e._v(" "),s("div",{staticClass:"text-center text-warning"},[e._v(e._s(e.hintTxt))])],1)],1)],1)]),e._v(" "),s("material-dialog",{on:{"on-check-material":e.checkMaterial}})],1)},staticRenderFns:[]};var v=s("VU/8")(p,m,!1,function(e){s("4PFM")},"data-v-89f8e622",null).exports,f=s("Dd8w"),g=s.n(f),b=s("Gu7T"),_=s.n(b),S=s("NYxO"),x={mounted:function(){var e=this;this.loadUserList(),this.$bus.$on("releaseUser",function(t){var s=e.__.findIndex(e.users,function(e){return e.id===t});e.users[s].selected=!1,e.users.splice(s,1,e.users[s]),e.updateIsAllSelected()}),this.$bus.$on("search-reload",function(){e.loadUserList()})},props:{tagSelected:Array,searchKey:String},data:function(){return{loading:!1,users:[],total:0,pageSize:10,curPage:1,isAllSelected:!1}},computed:g()({},Object(S.b)({selectedUserList:function(e){return e.push.selectedUserMap},selectedSize:function(e){return e.push.selectedSize}})),watch:{users:{handler:function(){this.updateIsAllSelected()},immediate:!0},selectedSize:{handler:function(){this.updateIsAllSelected()},immediate:!0}},methods:{updateIsAllSelected:function(){this.users&&this.users.length>0&&(this.isAllSelected=this.__.every(this.users,function(e){return console.log(e.selected),e.selected}))},loadUserList:function(){var e=this,t="";""!==this.searchKey.trim()&&(t=this.searchKey.trim()),this.loading=!0,this.api.push.loadUserList(this.tagSelected,this.curPage,this.pageSize,t).then(this.api.commonResp(function(t,s){t?(e.users=s.data,e.total=s.total,e.users.forEach(function(t){t.selected=!(void 0===e.selectedUserList["user"+t.id]||null==e.selectedUserList["user"+t.id])})):e.$message.error(s),e.loading=!1}))},handleSizeChange:function(e){this.pageSize=e,this.loadUserList()},handleCurrentChange:function(e){this.curPage=e,this.loadUserList()},toggleSelect:function(e,t){e.selected?this.$store.commit("push/releaseUsers",[e]):this.$store.commit("push/addUsers",[e]);var s=this.users[t];this.users.splice(t,1,s)},toggleAllSelect:function(){var e=this.users;this.isAllSelected?(this.$store.commit("push/releaseUsers",this.users),e.forEach(function(e){e.selected=!1})):(this.$store.commit("push/addUsers",this.users),e.forEach(function(e){e.selected=!0})),this.users=[].concat(_()(e))}}},C={render:function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("div",[s("el-table",{directives:[{name:"loading",rawName:"v-loading",value:e.loading,expression:"loading"}],staticClass:"manager-list",attrs:{data:e.users}},[s("el-table-column",{attrs:{width:"100"},scopedSlots:e._u([{key:"header",fn:function(t){return[s("el-button",{staticClass:"icon",class:[e.isAllSelected?"el-icon-minus":"el-icon-plus"],attrs:{type:e.isAllSelected?"":"primary"},on:{click:e.toggleAllSelect}})]}},{key:"default",fn:function(t){var a=t.row,i=t.$index;return[s("el-button",{staticClass:"icon",class:[a.selected?"el-icon-minus":"el-icon-plus"],attrs:{type:a.selected?"":"primary"},on:{click:function(t){return e.toggleSelect(a,i)}}})]}}])}),e._v(" "),s("el-table-column",{attrs:{label:"头像",width:"180"},scopedSlots:e._u([{key:"default",fn:function(e){var t=e.row;return[s("el-image",{staticClass:"headImg",attrs:{fit:"cover",src:t.headImg}})]}}])}),e._v(" "),s("el-table-column",{attrs:{prop:"name",label:"昵称",width:"140"}}),e._v(" "),s("el-table-column",{attrs:{prop:"sex",label:"性别",width:"120"}}),e._v(" "),s("el-table-column",{attrs:{prop:"phone",label:"手机号",width:"240"}}),e._v(" "),s("el-table-column",{attrs:{label:"用户标签","min-width":"200"},scopedSlots:e._u([{key:"default",fn:function(t){var a=t.row;return e._l(a.tags,function(t){return s("el-tag",{key:t.id,staticStyle:{margin:"5px"}},[e._v("\n        "+e._s(t.name)+"\n      ")])})}}])}),e._v(" "),s("el-table-column",{attrs:{label:"订阅时间",width:"240"},scopedSlots:e._u([{key:"default",fn:function(t){var a=t.row;return[s("i",{staticClass:"el-icon-time"}),e._v(" "),s("span",{staticStyle:{"margin-left":"10px"}},[e._v(e._s(e._f("formatTimeInMillis")(a.subscribeTime)))])]}}])})],1),e._v(" "),s("el-pagination",{staticClass:"fl",staticStyle:{margin:"10px"},attrs:{"current-page":e.curPage,"page-size":e.pageSize,layout:"total, sizes, prev, pager, next, jumper",total:e.total},on:{"size-change":e.handleSizeChange,"current-change":e.handleCurrentChange,"update:currentPage":function(t){e.curPage=t},"update:current-page":function(t){e.curPage=t}}})],1)},staticRenderFns:[]};var y=s("VU/8")(x,C,!1,function(e){s("i6zm")},"data-v-69cffc7a",null).exports,$=s("jd+X"),w={mounted:function(){this.userData.total=this.selectedSize},data:function(){return{userData:{total:0,pageSize:10,curPage:1,users:[]},uploadDialogVisible:!1,excelUploadAction:this.api.baseURL+"/push/users/excel",modelAction:this.api.baseURL+"/resource/template/phone",headers:{Authorization:this.$store.state.manager.token},loading:!1}},computed:g()({},Object(S.b)({selectedSize:function(e){return e.push.selectedSize}})),watch:{selectedSize:{handler:function(){this.update()},immediate:!0}},methods:{update:function(){this.loading=!0,this.userData.users=this.getSelectedUserList(),this.userData.total=this.selectedSize,this.loading=!1},handleCurrentChange:function(){this.update()},handleSizeChange:function(e){this.userData.pageSize=e,this.update()},addUsersToList:function(e){e.success?(this.$store.commit("push/addUsers",e.data),this.uploadDialogVisible=!1,this.$message("导入成功")):this.$message.error(e.msg)},showUploadExcelDialog:function(){this.uploadDialogVisible=!0},getSelectedUserList:function(){var e=this,t=[];i()(this.$store.state.push.selectedUserMap).forEach(function(s){var a=e.$store.state.push.selectedUserMap[s];a&&t.push(a)});var s=this.userData,a=s.pageSize,l=s.curPage;return t.slice((l-1)*a,l*a)},releaseUser:function(e){e.selected?(this.$store.commit("push/releaseUsers",[e]),this.$bus.$emit("releaseUser",e.id)):this.$store.commit("push/addUsers",[e])},toggleAllSelect:function(){var e=this;this.$store.commit("push/releaseUsers",this.userData.users),this.userData.users.forEach(function(t){e.$bus.$emit("releaseUser",t.id)})}},components:{Upload:$.a,BottomButtonDialog:h.a}},U={render:function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("div",[s("div",[s("el-button",{staticClass:"fr",attrs:{type:"primary",size:"mini"},on:{click:e.showUploadExcelDialog}},[s("b",{staticClass:"el-icon-receiving"}),e._v(" excel导入")])],1),e._v(" "),s("el-table",{staticClass:"manager-list",attrs:{data:e.userData.users,"v-loading":e.loading}},[s("el-table-column",{attrs:{width:"100"},scopedSlots:e._u([{key:"header",fn:function(t){return 0!==e.selectedSize?[s("el-button",{staticClass:"icon el-icon-minus",on:{click:e.toggleAllSelect}})]:void 0}},{key:"default",fn:function(t){var a=t.row,i=t.$index;return[s("el-button",{staticClass:"icon",class:[a.selected?"el-icon-minus":"el-icon-plus"],attrs:{type:a.selected?"":"primary"},on:{click:function(t){return e.releaseUser(a,i)}}})]}}],null,!0)}),e._v(" "),s("el-table-column",{attrs:{label:"头像",width:"180"},scopedSlots:e._u([{key:"default",fn:function(e){var t=e.row;return[s("el-image",{staticClass:"headImg",attrs:{fit:"cover",src:t.headImg}})]}}])}),e._v(" "),s("el-table-column",{attrs:{prop:"name",label:"昵称",width:"140"}}),e._v(" "),s("el-table-column",{attrs:{prop:"sex",label:"性别",width:"120"}}),e._v(" "),s("el-table-column",{attrs:{prop:"phone",label:"手机号",width:"240"}}),e._v(" "),s("el-table-column",{attrs:{label:"用户标签","min-width":"200"},scopedSlots:e._u([{key:"default",fn:function(t){var a=t.row;return e._l(a.tagList,function(t){return s("el-tag",{key:t.id,staticStyle:{margin:"5px"}},[e._v("\n          "+e._s(t.name)+"\n        ")])})}}])}),e._v(" "),s("el-table-column",{attrs:{label:"订阅时间",width:"240"},scopedSlots:e._u([{key:"default",fn:function(t){var a=t.row;return[s("i",{staticClass:"el-icon-time"}),e._v(" "),s("span",{staticStyle:{"margin-left":"10px"}},[e._v(e._s(e._f("formatTimeInMillis")(a.subscribeTime)))])]}}])})],1),e._v(" "),s("el-pagination",{staticClass:"fl",staticStyle:{margin:"10px"},attrs:{"current-page":e.userData.curPage,"page-size":e.userData.pageSize,layout:"total, sizes, prev, pager, next, jumper",total:e.userData.total},on:{"size-change":e.handleSizeChange,"current-change":e.handleCurrentChange,"update:currentPage":function(t){return e.$set(e.userData,"curPage",t)},"update:current-page":function(t){return e.$set(e.userData,"curPage",t)}}}),e._v(" "),s("bottom-button-dialog",{attrs:{visible:e.uploadDialogVisible,"bottom-visible":!1},on:{"update:visible":function(t){e.uploadDialogVisible=t}}},[s("template",{slot:"title"},[s("h2",[e._v("excel导入")])]),e._v(" "),[s("div",{attrs:{align:"center"}},[s("upload",{attrs:{action:e.excelUploadAction,"model-action":e.modelAction,"on-success":e.addUsersToList}})],1)]],2)],1)},staticRenderFns:[]};var k=s("VU/8")(w,U,!1,function(e){s("gB/q")},"data-v-7029c109",null).exports,z={mounted:function(){var e=this;this.api.push.getTagList().then(this.api.commonResp(function(t,s){t&&s.forEach(function(t){e.tags.push(t)})}))},data:function(){return{searchKey:"",activeTab:"用户列表",tagSelected:[0],tags:[{id:0,name:"全部"}]}},computed:g()({},Object(S.b)({selectedSize:function(e){return e.push.selectedSize}})),methods:{resetSearch:function(){this.searchKey="",this.tagSelected=[0]},search:function(){this.$bus.$emit("search-reload")}},components:{PushUserList:y,SelectedUserList:k}},I={render:function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("div",{staticClass:"select-receiver"},[s("div",{staticClass:"searcher"},[s("el-form",{attrs:{inline:!0}},[s("el-form-item",{attrs:{label:"用户标签"}},[s("el-select",{attrs:{multiple:""},model:{value:e.tagSelected,callback:function(t){e.tagSelected=t},expression:"tagSelected"}},e._l(e.tags,function(e){return s("el-option",{key:e.id,attrs:{label:e.name,value:e.id}})}),1)],1),e._v(" "),s("el-form-item",{attrs:{label:"关键字"}},[s("el-input",{attrs:{placeholder:"电话或用户昵称"},model:{value:e.searchKey,callback:function(t){e.searchKey=t},expression:"searchKey"}})],1),e._v(" "),s("el-form-item",{attrs:{align:"center"}},[s("el-button",{on:{click:e.resetSearch}},[e._v("重置")]),e._v(" "),s("el-button",{attrs:{type:"primary"},on:{click:e.search}},[e._v("搜索")])],1)],1)],1),e._v(" "),s("el-tabs",{model:{value:e.activeTab,callback:function(t){e.activeTab=t},expression:"activeTab"}},[s("el-tab-pane",{attrs:{label:"用户列表",name:"用户列表"}},[s("push-user-list",{attrs:{"tag-selected":e.tagSelected,"search-key":e.searchKey}})],1),e._v(" "),s("el-tab-pane",{attrs:{name:"已选列表"}},[s("span",{attrs:{slot:"label"},slot:"label"},[s("el-badge",{staticClass:"item",attrs:{value:e.selectedSize}},[e._v("\n          已选列表\n        ")])],1),e._v(" "),s("selected-user-list")],1)],1)],1)},staticRenderFns:[]};var L={components:{SelectReceiver:s("VU/8")(z,I,!1,function(e){s("pr7k")},"data-v-3de961b8",null).exports,PushInfo:v},data:function(){return{refresh:!0}},beforeDestroy:function(){this.$store.commit("push/clear")},methods:{onSendPush:function(e){var t=this,s=[];if(i()(this.$store.state.push.selectedUserMap).forEach(function(e){s.push(t.$store.state.push.selectedUserMap[e].id)}),s.length<2)this.$message.error("请至少选择两个用户");else{var a=this.$loading({lock:!0,text:"Loading",spinner:"el-icon-loading",background:"rgba(0, 0, 0, 0.7)"});this.api.push.sendPush(s,e).then(this.api.commonResp(function(e,s){e?(t.$store.commit("push/clear"),t.$router.push({name:"push"}),t.refresh=!1,t.$nextTick(function(){t.refresh=!0}),t.$message("推送成功")):t.$message.error(s)})).finally(function(){a.close()})}}}},M={render:function(){var e=this.$createElement,t=this._self._c||e;return this.refresh?t("div",[t("el-row",[t("el-col",[t("push-info",{on:{"send-push":this.onSendPush}})],1)],1),this._v(" "),t("el-row",[t("el-col",[t("select-receiver")],1)],1)],1):this._e()},staticRenderFns:[]};var P=s("VU/8")(L,M,!1,function(e){s("p0gL")},"data-v-0a06ba8f",null);t.default=P.exports},"gB/q":function(e,t){},i6zm:function(e,t){},iq8Z:function(e,t){},p0gL:function(e,t){},pr7k:function(e,t){}});
//# sourceMappingURL=4.c05d6f1ebf9e327ec6c1.js.map