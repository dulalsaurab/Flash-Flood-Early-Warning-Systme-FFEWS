/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package majorproject;

import com.esri.arcgis.beans.TOC.TOCBean;
import com.esri.arcgis.beans.globe.GlobeBean;
import com.esri.arcgis.beans.map.MapBean;
import com.esri.arcgis.beans.scene.SceneBean;
import com.esri.arcgis.beans.symbology.SymbologyBean;
import com.esri.arcgis.beans.toolbar.ToolbarBean;

/**
 *
 * @author suraj
 */
public class BeanFactory{
    public MapBean getMapBean(){
        return new MapBean();
    }
    public GlobeBean getGlobeBean(){
        return new GlobeBean();
    }
    public TOCBean getTocBean(){
        return new TOCBean();
    }
    public SceneBean getSceneBean(){
        return new SceneBean();
    }
    public SymbologyBean getSymbologyBean(){
        return new SymbologyBean();
    }
    public ToolbarBean getToolbarBean(){
        return new ToolbarBean();
    }
}
