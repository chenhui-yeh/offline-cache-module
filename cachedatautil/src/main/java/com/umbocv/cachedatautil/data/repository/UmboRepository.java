package com.umbocv.cachedatautil.data.repository;

import com.umbocv.cachedatautil.data.local.UmboDao;
import com.umbocv.cachedatautil.data.model.UmboObject;

import java.util.List;

public interface UmboRepository {

     List<UmboObject> loadData(String authToken);

     UmboObject loadSingleData (String authToken);

     void saveData(UmboObject ... objects);

     void deleteData(UmboObject object);


}
