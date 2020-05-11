/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React from 'react';
import {
  SafeAreaView,
  StyleSheet,
  ScrollView,
  View,
  Text,
  StatusBar,
  Button,NativeModules,PermissionsAndroid
} from 'react-native';

function App(){
  return(
    <View style={{flex:1}}>
       <View style={{flex:1,justifyContent:'center',alignItems:'center'}}>
        <Button style={{width:300,height:300,margin:10,padding:10}}
            onPress={() =>  NativeModules.ActivityStarter.navigateToExample()}
            title='X-Ray'
          />
          <Button style={{width:300,height:300,margin:50}}
            onPress={() =>  NativeModules.ActivityStarter.navigateToMRI()}
            title='MRI'
          />
          </View>
    </View>
  );
}
function onPress(){
  var that = this;
  //Checking for the permission just after component loaded
  async function requestCameraPermission() {
    //Calling the permission function
    const granted = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.READ_EXTERNAL_STORAGE,
      {
        title: 'App need Permission',
        message: 'Svasthiya App needs access to your Storage ',
      }
    );
    if (granted === PermissionsAndroid.RESULTS.GRANTED) {
      NativeModules.ActivityStarter.navigateToExample()
    } else {
      requestCameraPermission();
    }
  }
  if (Platform.OS === 'android') {
    requestCameraPermission();
  } else {
    NativeModules.ActivityStarter.navigateToExample()
  }
};

export default App;
