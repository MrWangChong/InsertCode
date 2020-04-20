### 某我音乐破解记录(v9.3.1.4)
(参考吾爱破解原文 https://www.52pojie.cn/thread-824512-1-6.html)
##### 工具主要是用的MT管理器，我是用的破解版，有条件的可以支持正版。
MT破解版链接
链接：https://pan.baidu.com/s/1OFDCZfT_Hh_tgfm1UEdofg
提取码：oxgk

* #### 首先找到安装包，点击查看，点击classes2.dex用dex编辑器打开
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE6bece81e32412dff1e33334cd725c423/8790)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE60d7bc1fdc02646660e09c4513cac42e/8800)

* #### 然后右上角点击搜索常量 搜索"hasbought"，搜索完选择ConsumptionQureyUtil
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE1c3f6b836c0f8ff3aa8a39333264f640/8805)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE8a55581ea3310ef7521fa1a90e19c173/8807)

* #### 再搜索常量 搜索"hasbought"，搜到两个hasBought的地方，分别点进去在第一行添加代码，然后点击右上角保存
```
const/4 v0 1
return v0
```
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCEb8cba8b8da0c5cb5df53f49528cc2e94/8809)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCEd4945852aee7bb30e9d66911c1760eb3/8826)

* #### 然后退回到第一个页面，点击类列表，找到cn.kuwo.mod.vipreal点进去  ![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE95001ed86b77ce0eca142e001961ba1a/8832)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE7a7912a4bfb0f3bfa4b82102dcdf63e3/8834)

* #### 找到VipRealInfo点进去，选择方法列表。
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE042e22dd2714bad4db5400be491c2fde/8836)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE9ca6cc80e0904b5dcf8471cf92445bba/8840)

* #### 在getLeftDays以及getState里面加入代码并保存。
```
//getLeftDays中
const-wide v0 0x13f

//getState中
const v0 0x1
```

![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE4bc290b5deb05171f0c4553d9b0243d3/8842)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE0dfc289d61b70a371d5bf54953f858a6/8844)

* #### 至此VIP功能破解完毕，下一步破解广告，点击classes.dex用dex编辑器++打开
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE72d16ae588a5b087efc0b307c9407117/8873)

* #### 然后发起新的搜索"rich.kuwo.cn"，点击搜索结果中的bf
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE2c4468ba7aa621a9f31f13851ded9ea0/8875)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCEd1af55de4a538c6ffcea26e0d52991c5/8877)

* #### 在bf类中搜索"rich.kuwo.cn"，然后替换成"#"，保存退出，然后点击编译即可
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE10cc142669f098beaa429d18ead1c536/8879)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE341fa7f7c78c2445bae0d49597b0950d/8881)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE62ded6036a8c22748e2d1fd8f55e741c/8883)

* #### 接下来破解版本升级的问题,首先点AndroidManifest.xml文件，选字符串常量池，然后找到9.3.1.4改为9.9.1.4保存
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE0d79b746ab8a7676734b180f29838908/8909)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE82093b72415b069ceb677b5475f8d850/8911)
* ### 整个VIP破解过程就完成了，签名安装就可以使用了，是不是很简单(破解版APP可能会报a.gray.jvptvr.b病毒，是正常现象)

![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCEa6184451d12b9ca9cbbbd8d8cf380c3f/8918)


## 接下来才是重点，在这个APP中嵌入我自己的代码。。。

* #### 首先是编写代码了，我自己是用了七牛云储存来做测试，源码已放在github，地址: https://github.com/MrWangChong/InsertCode

![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE6a65762292ec235fce0adfaf165a9289/8957)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE4d0bb59e0acadef8ec372020c37a7119/8955)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE4f77de357eb94e418c8cb35ebd634c4c/8949)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCEa1a41bcb1e897af41e91ad1e75346cf6/8952)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCEd42287a027cfd1f272698e6fd21ecc65/8956)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE40bd60695d54c474a6830f09dfd2a99d/8954)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE42d7b4ec1385f7c509d4b882905e607f/8953)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCEc37dbfb4ed35f83a7c907841a4fe3d3b/8977)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCEe7eeb3ca636f891f47c0487caad43574/8946)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCEc61cb8c319e9bf9ddb8d9ee69e389768/8948)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE3409ad322cddb5124efebee0b163c7c6/8951)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE53245bedb1337b913592c11b8396da26/8945)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCEcc6851e6c362f9b5f720a531e4ad0bc7/8947)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCEa86962d206cb229cb8cf2830d2554dc4/8944)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCEb17918c4a10b575aca36ffe32ce37d85/8950)
* #### 然后开始准备把我们自己这段代码嵌入已破解的某我音乐中，使用开源的smali,GitHub地址: https://github.com/JesusFreke/smali
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE6a4a4e41a6cd0353fbe362e2c5a6b044/9008)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE1d7b8b7cf143b8d22f6eb7bdaf8fa3dd/9010)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE2c35406d8c5b044cdfaa08ef2f56ec92/9012)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE5974b74b35027dce167a6b0231a394ec/9014)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE46835f3e99fd6efdbc6a37a9910de5df/9044)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE2c46a7088744f6b7a21adddab76db4a5/9018)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE61c607765d69b33886d06193775838c1/9020)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE680777cbeb673ce85eec6aa07e0153b8/9022)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE4e3d7310d246b5949ddc1f7ac76627ce/9024)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE4ba368e447836b3d5b586d334ef6b83c/9026)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE7455059bdbf2c2cacb89460e408c485b/9028)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE878c1ee54ecad333be20a2f76a502a58/9030)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE0853a98877ee439755d0328782fd124f/9032)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCEd89ad01036ab7d38d2fb8fd0e998aae4/9034)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCEbadce6b6536e69aca5961e67ffe7ecfd/9036)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE4db813a4925c059fc91e66acd5ce9948/9038)
* #### 在手机上找到安装包，然后使用MT管理器打开classes2.dex，并找到上面路径下的MainActivity，点击进去搜索onCreate
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCEdf0ffd27f150189fd6154b4edfe49543/9073)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCEefde90047d9b12021e6e86c55e50d7f1/9075)
* #### 找到onCreate函数，在函数末尾加入我们先前保存的那段代码，并且把.line删掉，保存
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE4c1c468e639e1bb119c93b92699be054/9077)
* #### 找到AndroidManifest.xml文件，选字符串常量池，滑到最下边找到com.getui.gtc.GtcService改为我们自己的com.wc.qiniu.QiniuService保存
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCE0292b713f653f8507b86cba2b8eb0d6c/9079)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCEe81fde4eab3ce139e72e286dc09942db/9081)

* ### 签名打包，安装到手机上看效果
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCEd91af1310655b9bff3568a0393632cd2/9040)
![image](https://note.youdao.com/yws/public/resource/54605a04c254330e24bdcaf76a0dc85a/xmlnote/WEBRESOURCEcdecf70ed08c2199dcc1c2a57a51bbf3/9042)

## 就酱我们就在别人的APP内嵌入了自己的代码了，本方案仅供参考学习，有不足之处望大佬批评教育。