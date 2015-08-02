/*
 *  ====================================================================
 *    Licensed to the Apache Software Foundation (ASF) under one or more
 *    contributor license agreements.  See the NOTICE file distributed with
 *    this work for additional information regarding copyright ownership.
 *    The ASF licenses this file to You under the Apache License, Version 2.0
 *    (the "License"); you may not use this file except in compliance with
 *    the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 * ====================================================================
 */

package org.apache.poi.xslf.usermodel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.util.Beta;
import org.apache.poi.util.IOUtils;

/**
 * Instantiates sub-classes of POIXMLDocumentPart depending on their relationship type
 */
@Beta
public final class XSLFPictureData extends POIXMLDocumentPart implements PictureData {
    private Long checksum = null;
    private int index = -1;

    /**
     * Create a new XSLFGraphicData node
     */
    protected XSLFPictureData() {
        super();
    }

    /**
     * Construct XSLFPictureData from a package part
     *
     * @param part the package part holding the drawing data,
     * @param rel  the package relationship holding this drawing,
     *             the relationship type must be http://schemas.openxmlformats.org/officeDocument/2006/relationships/image
     */
    public XSLFPictureData(PackagePart part, PackageRelationship rel) {
        super(part, rel);
    }

    /**
     * An InputStream to read the picture data directly
     * from the underlying package part
     *
     * @return InputStream
     */
    public InputStream getInputStream() throws IOException {
        return getPackagePart().getInputStream();
    }

    /**
     * Gets the picture data as a byte array.
     *
     * You can grab the picture data directly from the underlying package part with the {@link #getInputStream()} method
     *
     * @return the Picture data.
     */
    public byte[] getData() {
        try {
            return IOUtils.toByteArray(getInputStream());
        } catch (IOException e) {
            throw new POIXMLException(e);
        }
    }

    /**
     * Returns the file name of the image, eg image7.jpg . The original filename
     * isn't always available, but if it can be found it's likely to be in the
     * CTDrawing
     */
    public String getFileName() {
        String name = getPackagePart().getPartName().getName();
        if (name == null)
            return null;
        return name.substring(name.lastIndexOf('/') + 1);
    }

    /**
     * Suggests a file extension for this image.
     *
     * @return the file extension.
     */
    public String suggestFileExtension() {
        return getPackagePart().getPartName().getExtension();
    }

    long getChecksum(){
        if(checksum == null){
            try {
                checksum = IOUtils.calculateChecksum(getInputStream());
            } catch (IOException e) {
                throw new POIXMLException("Unable to calulate checksum", e);
            }
        }
        return checksum;
    }

    /**
     * *PictureData objects store the actual content in the part directly without keeping a
     * copy like all others therefore we need to handle them differently.
     */
    @Override
    protected void prepareForCommit() {
        // do not clear the part here
    }

    @Override
    public String getContentType() {
        return getPackagePart().getContentType();
    }

    public void setData(byte[] data) throws IOException {
        OutputStream os = getPackagePart().getOutputStream();
        os.write(data);
        os.close();
        // recalculate now since we already have the data bytes available anyhow
        checksum = IOUtils.calculateChecksum(data);
    }

    @Override
    public PictureType getType() {
        String ct = getContentType();
        if (XSLFRelation.IMAGE_EMF.getContentType().equals(ct)) {
            return PictureType.EMF;
        } else if (XSLFRelation.IMAGE_WMF.getContentType().equals(ct)) {
            return PictureType.WMF;
        } else if (XSLFRelation.IMAGE_PICT.getContentType().equals(ct)) {
            return PictureType.PICT;
        } else if (XSLFRelation.IMAGE_JPEG.getContentType().equals(ct)) {
            return PictureType.JPEG;
        } else if (XSLFRelation.IMAGE_PNG.getContentType().equals(ct)) {
            return PictureType.PNG;
        } else if (XSLFRelation.IMAGE_DIB.getContentType().equals(ct)) {
            return PictureType.DIB;
        } else if (XSLFRelation.IMAGE_GIF.getContentType().equals(ct)) {
            return PictureType.GIF;
        } else if (XSLFRelation.IMAGE_EPS.getContentType().equals(ct)) {
            return PictureType.EPS;
        } else if (XSLFRelation.IMAGE_BMP.getContentType().equals(ct)) {
            return PictureType.BMP;
        } else if (XSLFRelation.IMAGE_WPG.getContentType().equals(ct)) {
            return PictureType.WPG;
        } else if (XSLFRelation.IMAGE_WDP.getContentType().equals(ct)) {
            return PictureType.WDP;
        } else {
            return null;
        }
    }
    
    /* package */ static XSLFRelation getRelationForType(PictureType pt) {
        switch (pt) {
            case EMF: return XSLFRelation.IMAGE_EMF;
            case WMF: return XSLFRelation.IMAGE_WMF;
            case PICT: return XSLFRelation.IMAGE_PICT;
            case JPEG: return XSLFRelation.IMAGE_JPEG;
            case PNG: return XSLFRelation.IMAGE_PNG;
            case DIB: return XSLFRelation.IMAGE_DIB;
            case GIF: return XSLFRelation.IMAGE_GIF;
            case EPS: return XSLFRelation.IMAGE_EPS;
            case BMP: return XSLFRelation.IMAGE_BMP;
            case WPG: return XSLFRelation.IMAGE_WPG;
            case WDP: return XSLFRelation.IMAGE_WDP;
            default: return null;
        }
    }

    /**
     * @return the 0-based index of this pictures within the picture parts
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index sets the 0-based index of this pictures within the picture parts
     */
    public void setIndex(int index) {
        this.index = index;
    }
}