Prima di eseguire, è necessario modificare l'indirizzo IP del server nella costante BASE_URL nel file NoteSharingApi.

Nota: Abbiamo riscontrato problemi con la mapView su alcuni emulatori, quindi il funzionamento ottimale è garantito solo su dispositivi reali.

Attenzione: è richiesto almeno Android API 33 (Android 13) per il corretto funzionamento, poiché è necessario per il metodo getGeoPointFromAddress in Utility, che trasforma un indirizzo in geocoordinate.