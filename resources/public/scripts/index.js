(function (services, geoLocation, Vue) {
    const resultBox = '#result-box';

    const getPosition = function (options) {
        return new Promise(function (resolve, reject) {
            geoLocation.getCurrentPosition(resolve, reject, options);
        });
    };

    window.initMap = async function () {
        const { gasstations, lat, lon } = await getNearByGasStations();
        const map = new google.maps.Map(document.getElementById('map'), {
            zoom: 12,
            center: { lat: lat, lng: lon }
        });

        gasstations.forEach(g => toMarkers(g, map));

        window.template = new Vue({
            el: resultBox,
            data: { gasstations }
        });
    };

    const toMarkers = function ({ address, city, distance, name, lat, lon }, map) {
        return new google.maps.Marker({
            position: { lat: lat, lng: lon },
            map: map
        })
    };

    const getNearByGasStations = async function () {
        try {
            const { coords: { latitude, longitude } } = await getPosition();
            return await services.getNearByGasStations(latitude, longitude);
        } catch (err) {
            console.warn('The user rejected to provide location');
            return await services.getNearByGasStations();
        }
    };

}(services, navigator.geolocation, window.Vue));