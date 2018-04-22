/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.samples.vision.ocrreader;

import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.samples.vision.ocrreader.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;

import java.util.ArrayList;

/**
 * A very simple Processor which receives detected TextBlocks and adds them to the overlay
 * as OcrGraphics.
 */
public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {

    private GraphicOverlay<OcrGraphic> mGraphicOverlay;
    private ArrayList<String> allTextCaptured;
    private TextBlock[] allTextBlock;

    OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay) {
        mGraphicOverlay = ocrGraphicOverlay;
        allTextCaptured = new ArrayList<>();
    }

    /**
     * Called by the detector to deliver detection results.
     * If your application called for it, this could be a place to check for
     * equivalent detections by tracking TextBlocks that are similar in location and content from
     * previous frames, or reduce noise by eliminating TextBlocks that have not persisted through
     * multiple detections.
     */
    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        mGraphicOverlay.clear();
        SparseArray<TextBlock> items = detections.getDetectedItems();
        allTextBlock = new TextBlock[items.size()];
        String text = "";
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            text = text + item.getValue();
            allTextBlock[i] = item;
            //allTextCaptured.add(item.getValue());
            //OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item);
            //mGraphicOverlay.add(graphic);
        }
        //TODO puoi togliere l'ordinamento, fatto male 1, già implementato 2
        orderTextBlock();
    }

    private void orderTextBlock(){
        for(int i = 0;i<allTextBlock.length; i++) {
            for(int k = i+1;k<allTextBlock.length; k++) {
                if(allTextBlock[i].getBoundingBox().top>allTextBlock[k].getBoundingBox().top) {
                    TextBlock tmp = allTextBlock[i];
                    allTextBlock[i] = allTextBlock[k];
                    allTextBlock[k] = tmp;
                }
            }
        }
    }

    private void addTextInOrder(TextBlock textBlock){

        for(int i = 0; i<allTextBlock.length;i++){
            if(textBlock.getBoundingBox().top>=allTextBlock[i].getBoundingBox().top){
                TextBlock temp = allTextBlock[i];
                allTextBlock[i] = temp;

            }
        }
    }

    /**
     * Frees the resources associated with this detection processor.
     */
    @Override
    public void release() {
        mGraphicOverlay.clear();
    }

    public String getAllTextCaptured(){
        String result = "";
        for(int i = 0; i<allTextBlock.length;i++){
            allTextCaptured.add(allTextBlock[i].getValue());
        }
        for (String currentText:allTextCaptured
             ) {
            result = result + " " + currentText;
        }
        Log.i("real-time",result);
        return result;
    }
}
