// IWebServiceInterface.aidl
package com.sloth.cinema;

import com.sloth.cinema.IWebServiceCallbackInterface;

interface IWebServiceInterface {

    void addCallback(IWebServiceCallbackInterface callback);

    void removeCallback(IWebServiceCallbackInterface callback);

    void start();

}