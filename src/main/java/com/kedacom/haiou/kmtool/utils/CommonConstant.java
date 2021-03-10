package com.kedacom.haiou.kmtool.utils;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2021/2/22.
 */

public class CommonConstant {

    public static final String QUERY_FACE = "/VIID/Faces";
    public static final String QUERY_PERSON = "/VIID/Persons";

    public static final String FACE_FIELDS = "FaceID, RelativeID, Name, IDNumber, TabID, EntryTime, SubImageList.StoragePath";
    public static final String PERSON_FIELDS = "PersonID, Name, IDNumber, TabID, EntryTime, SubImageList.StoragePath";

    public static final String CREATE_REPOSITORY = "/addRepository";

    public static final HashMap<String, String> ALGORITHM_MAP = new HashMap<String, String>() {
        {
            put("ALGORITHM_4", "");
            put("ALGORITHM_5", "http://86.81.137.226:19091/faceEngine/sensetime");
            put("ALGORITHM_6", "http://86.81.130.19:17050/faceEngine/hikvision");
        }
    };

    public static final HashMap<String, String> ALGORITHM_REPO_MAP = new HashMap<String, String>() {
        {
            put("ALGORITHM_4", "bc0847c8be4c9a08d3936fb161824876");
            put("ALGORITHM_5", "3d509de8-2b91-4329-b64a-14cfbee1a000");
            put("ALGORITHM_6", "1615192394149");
        }
    };
}
