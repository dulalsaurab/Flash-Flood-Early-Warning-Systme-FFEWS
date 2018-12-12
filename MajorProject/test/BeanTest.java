/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.esri.arcgis.beans.map.MapBean;
import majorproject.BeanFactory;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author suraj
 */
public class BeanTest {

    @Test
    public void hello() {
//        BeanFactory beanFactory = new BeanFactory();
//        MapBean mapBean = beanFactory.getMapBean();
        MapBean mapBean = new MapBean();
        
        assertNotNull(mapBean);
        System.out.println(mapBean);
    }
}
