/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package majorproject.test;

import com.esri.arcgis.beans.map.MapBean;
import static org.junit.Assert.assertNotNull;

/**
 *
 * @author suraj
 */
public class BeanTest {
    public static void main(String[] args) {
        MapBean mapBean = new MapBean();
        
        assertNotNull(mapBean);
        System.out.println(mapBean);
    }
}
