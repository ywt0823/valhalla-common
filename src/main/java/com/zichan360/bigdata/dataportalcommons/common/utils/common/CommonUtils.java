package com.zichan360.bigdata.dataportalcommons.common.utils.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.List;

/**
 * CommonUtils
 *
 * @author sunweihong
 * @desc 公共工具类
 * @date 2020/10/14 17:13
 **/
public class CommonUtils {

    /**
     * 从列表数据生成树状json
     *
     * @param datas 列表数据
     * @param rootNodeValue 根节点唯一标识的值
     * @param propertyNameOfRelationNode 两级相关联的属性名称
     * @param idName tree node的唯一标识
     * @return 树状json
     */
    public static JSONArray generateTreeFromList(List<?> datas, String rootNodeValue, String propertyNameOfRelationNode, String idName) {
        JSONObject root = new JSONObject();
        root.put(idName, rootNodeValue);
        root.put("child", new JSONArray());
        for (Object data : datas) {
            JSONObject jsonNode = JSON.parseObject(JSON.toJSONString(data, SerializerFeature.WriteNullStringAsEmpty));
            String relationPropertyVaule = jsonNode.getString(propertyNameOfRelationNode);
            JSONObject treeNode = findTreeNode(root, idName, relationPropertyVaule);
            JSONArray child = new JSONArray();
            if(treeNode.containsKey("child")) {
                child = treeNode.getJSONArray("child");
            }
            child.add(jsonNode);
            treeNode.put("child",child);
        }
        return root.getJSONArray("child");
    }


    /**
     * 从树状json中遍历寻找node节点
     * @param originObject 树状json对象
     * @param idName 唯一标识的属性
     * @param idValue 唯一标识的值
     * @return 查找的节点
     */
    private static JSONObject findTreeNode(JSONObject originObject, String idName, String idValue) {
        JSONObject result = null;
        String nodeIdVaule = originObject.getString(idName);
        if (idValue.equals(nodeIdVaule)) {
            //如果要查找的是当前对象
            result = originObject;
        } else {
            //去子节点中查询
            if(originObject.containsKey("child")){
                JSONArray nodeChilds = originObject.getJSONArray("child");
                for (int i = 0; i < nodeChilds.size(); i++) {
                    JSONObject childNode = nodeChilds.getJSONObject(i);
                    JSONObject nextLevelChildNode = findTreeNode(childNode, idName, idValue);
                    if (nextLevelChildNode != null) {
                        result = nextLevelChildNode;
                        break;
                    }
                }
            }
        }
        return result;
    }
}
