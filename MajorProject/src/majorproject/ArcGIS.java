/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package majorproject;

import com.esri.arcgis.system.AoInitialize;
import com.esri.arcgis.system.esriLicenseExtensionCode;
import com.esri.arcgis.system.esriLicenseProductCode;
import com.esri.arcgis.system.esriLicenseStatus;


/**
 *
 * @author suraj
 */
public class ArcGIS {

    public static void initializeArcGISLicenses() {
        try {
            AoInitialize ao = new AoInitialize();
            if (ao.isProductCodeAvailable(esriLicenseProductCode.esriLicenseProductCodeEngine)
                    == esriLicenseStatus.esriLicenseAvailable) {
                ao.initialize(esriLicenseProductCode.esriLicenseProductCodeEngine);
            } else if (ao.isProductCodeAvailable(esriLicenseProductCode.esriLicenseProductCodeBasic)
                    == esriLicenseStatus.esriLicenseAvailable) {
                ao.initialize(esriLicenseProductCode.esriLicenseProductCodeBasic);
            } else if (ao.isProductCodeAvailable(esriLicenseProductCode.esriLicenseProductCodeStandard)
                    == esriLicenseStatus.esriLicenseAvailable) {
                ao.initialize(esriLicenseProductCode.esriLicenseProductCodeStandard);
            } else if (ao.isProductCodeAvailable(esriLicenseProductCode.esriLicenseProductCodeAdvanced)
                    == esriLicenseStatus.esriLicenseAvailable) {
                ao.initialize(esriLicenseProductCode.esriLicenseProductCodeAdvanced);
            }

            ao.checkOutExtension(esriLicenseExtensionCode.esriLicenseExtensionCode3DAnalyst);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(" Program Exit: Unable to check out proper licenses");
            System.exit(0);
        }
    }
}
