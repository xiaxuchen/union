package com.originit.union.api.controller;

import com.originit.common.exceptions.ParameterInvalidException;
import com.originit.common.page.Pager;
import com.originit.common.util.POIUtil;
import com.originit.common.util.RedisCacheProvider;
import com.originit.common.validator.group.CreateGroup;
import com.originit.union.api.util.ShiroUtils;
import com.originit.union.api.wxinterceptor.PreviewQRCodeInterceptor;
import com.originit.union.bussiness.MaterialBusiness;
import com.originit.union.bussiness.MessageBusiness;
import com.originit.union.constant.SystemConstant;
import com.originit.union.entity.domain.PreviewState;
import com.originit.union.entity.dto.PushInfoDto;
import com.originit.union.entity.vo.IndexStatisticVO;
import com.originit.union.entity.vo.MaterialVO;
import com.originit.union.entity.vo.TagInfoVO;
import com.originit.union.entity.vo.UserInfoVO;
import com.originit.union.service.PushService;
import com.originit.union.service.RedisService;
import com.originit.union.service.TagService;
import com.originit.union.service.WeChatUserService;
import com.originit.union.util.DataUtil;
import com.soecode.wxtools.api.IService;
import com.soecode.wxtools.exception.WxErrorException;
import com.xxc.response.anotation.ResponseResult;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * @author xxc、
 */
@RestController
@RequestMapping("/push")
@ResponseResult
public class PushController {
    private static  final  String mycardcode="851357382948";
    /**
        夏openid
     */
    private String opendid1="o1U3Tjj8m_Kqq9tJzT7B10Uj4NoA";
    /**
        执念openid
     */
    private  String openid="o1U3TjoBfIKeo_dyR380-Z4Vw_vU";
    private IService wxService;

    private WeChatUserService userService;

    private TagService tagService;

    private MaterialBusiness materialBusiness;

    private MessageBusiness messageBusiness;

    private RedisCacheProvider redisCacheProvider;

    private PushService pushService;

    private RedisService redisService;

    @Autowired
    public void setRedisService(RedisService redisService) {
        this.redisService = redisService;
    }

    @Autowired
    public void setPushService(PushService pushService) {
        this.pushService = pushService;
    }

    @Autowired
    public void setRedisCacheProvider(RedisCacheProvider redisCacheProvider) {
        this.redisCacheProvider = redisCacheProvider;
    }

    @Autowired
    public void setWxService(IService wxService) {
        this.wxService = wxService;
    }

    @Autowired
    public void setUserService(WeChatUserService userService){
        this.userService=userService;
    }

    @Autowired
    public void setTagService(TagService tagService) {
        this.tagService = tagService;
    }

    @Autowired
    public void setMaterialBusiness(MaterialBusiness materialBusiness) {
        this.materialBusiness = materialBusiness;
    }

    @Autowired
    public void setMessageBusiness(MessageBusiness messageBusiness) {
        this.messageBusiness = messageBusiness;
    }

    /**
     * 查找用户信息并返回
     * @param searchKey 搜索的关键字
     * @param tagList 用户标签列表
     * @param curPage  当前页
     * @param pageSize  一页显示几条数据
     * @return  用户信息列表
     */
    @GetMapping("/userList")
    public Pager<UserInfoVO> searchUserList(@RequestParam(required = false,defaultValue = "") String searchKey,
                                            @RequestParam(required = false) List<Integer> tagList,
                                            @RequestParam(required = false,defaultValue = "1") int curPage,
                                            @RequestParam(required = false,defaultValue = "10") int pageSize) {
        // 如果包含id为0的标签则为选择了所有标签
        if(tagList != null && tagList.stream().anyMatch(id -> id == 0)) {
            tagList.clear();
        }
        return userService.getUserInfoList(searchKey,
                tagList, curPage, pageSize);
    }

    /**
     * 获取用户的所有标签
     */
    @RequestMapping ("/tagList")
    @ResponseBody
    public List<TagInfoVO> getTagList() {
       return  tagService.getTagList();
    }

    /**
     * 获取图文素材信息
     * @param curPage 当前页
     * @param pageSize 每页多少个素材
     */
    @GetMapping("/materials")
    public List<MaterialVO> getMaterialList(@RequestParam int curPage,
                                            @RequestParam int pageSize) {
        return  materialBusiness.getMaterialList(curPage,pageSize);
    }

    /**
     * 预览消息，获取预览的二维码
     * @param type 消息类型
     * @param content 消息内容
     * @return 预览二维码的URL
     */
    @PostMapping("/preview")
    public Map<String,String> preview(@RequestParam String id, @RequestParam Integer type, @RequestParam String content)  {
        String msgType = messageBusiness.getMsgType(type);
        if (id != null && !id.trim().isEmpty()) {
            messageBusiness.preview(id,msgType,content);
            return new HashMap<>(0);
        }
        if (content.trim().isEmpty()) {
            throw new ParameterInvalidException("推送内容不能为空");
        }
        String item = UUID.randomUUID().toString().replace("-","");
        int expireTime = 180;
        // 将其放入redis中缓存
        redisCacheProvider.hset(PreviewQRCodeInterceptor.EVENT_KEY,item,
                new PreviewState(item,false,msgType,content),expireTime);
        String qrUrl = messageBusiness.generateTempQRCode(PreviewQRCodeInterceptor.EVENT_KEY + "?id=" + item, expireTime);
        return DataUtil.<String,String>mapBuilder()
                .append("pushItemId",item)
                .append("url",qrUrl)
                .build();
    }

    /**
     * 获取当前预览的状态
     * @param itemId 预览id
     * @return 预览状态
     */
    @GetMapping("/preview/state")
    public Integer previewState (@RequestParam String itemId) {
        final PreviewState previewState = (PreviewState) redisCacheProvider.hget(PreviewQRCodeInterceptor.EVENT_KEY, itemId);
        if (previewState == null) {
            // 过期或不存在
            return -1;
        }
        if (previewState.getSuccess()) {
            // 从中移除
            redisCacheProvider.hdel(PreviewQRCodeInterceptor.EVENT_KEY,itemId);
            // 预览发送成功
            return 1;
        } else {
            // 暂未发送
            return 0;
        }
    }


    /**
     * 通过用户电话的excel获取用户的openid
     * @param file
     * @return
     * @throws IOException
     */
    @RequestMapping("/users/excel")
    @ResponseBody
    public List<UserInfoVO> getUserInfoById(MultipartFile file) throws IOException {
        List<String> phones = POIUtil.customQuery(file.getInputStream(),file.getOriginalFilename(), workbook -> {
           List<String> list = new ArrayList<>();
            Sheet sheet = workbook.getSheetAt(0);
            // 从第二行开始
            for (int row = 1; row <= sheet.getLastRowNum(); row++) {
                Row rowObj = sheet.getRow(row);
                for (int col = 0; col < 1; col++) {
                    list.add(POIUtil.getCellValue(rowObj.getCell(col)));
                }
            }
            return list;
        });
        return userService.getUserInfoByPhones(phones);
    }

    /**
     *添加用户推送信息
     * @param pushInfo  推送的信息，type为1表示文本消息，为2表示图文消息，content对应为文本内容和微信公众平台的media_id
     */
    @PostMapping("/push")
    public void addPushInfo(@Validated({CreateGroup.class})  @RequestBody PushInfoDto pushInfo) throws WxErrorException, IOException {
        Long pushId = messageBusiness.pushMessage(pushInfo);
        Long userId = ShiroUtils.getUserInfo().getUserId();
        pushInfo.setPushId(pushId);
        pushInfo.setPusher(userId);
        pushService.addPushInfo(pushInfo);
    }

    /**
     * 获取首页统计数据
     * @return
     */
    @GetMapping("/index")
    public IndexStatisticVO getIndexStatistic (@RequestParam(required = false) String start,@RequestParam(required = false) String end) {
        final IndexStatisticVO pushStatistic = pushService.getPushStatistic(start, end);
        pushStatistic.setAllUserCount((Integer) redisCacheProvider.get(SystemConstant.ALL_USER_COUNT));
        pushStatistic.setBindUserCount((Integer) redisCacheProvider.get(SystemConstant.USER_BIND_COUNT));
        pushStatistic.setLastDayUserAddCount((Integer) redisCacheProvider.get(SystemConstant.USER_YESTERDAY_ADDITION));
        pushStatistic.setTheMonthUserAddCount((Integer) redisCacheProvider.get(SystemConstant.USER_MONTH_ADDITION));
        return pushStatistic;
    }
}


