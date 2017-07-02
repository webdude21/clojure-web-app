(function (services, geoLocation, Map, Marker) {
    const resultBox = document.getElementById('result-box');

    const getPosition = function (options) {
        return new Promise(function (resolve, reject) {
            geoLocation.getCurrentPosition(resolve, reject, options);
        });
    };

    window.initMap = async function () {
        const {gasstations, lat, lon} = await getNearByGasStations();
        const map = new Map(document.getElementById('map'), {
            zoom: 12,
            center: {lat: lat, lng: lon}
        });

        gasstations.forEach(g => toMarkers(g, map));
        renderResult(resultBox, gasstations)
    };

    const toMarkers = function ({address, city, distance, name, lat, lon}, map) {
        return new Marker({
            position: {lat: lat, lng: lon},
            map: map
        })
    };

    const getNearByGasStations = async function () {
        try {
            const {coords: {latitude, longitude}} = await getPosition();
            return await services.getNearByGasStations(latitude, longitude);
        } catch (err) {
            console.warn('The user rejected to provide location');
            return await services.getNearByGasStations();
        }
    };


    const toListItem = function (text) {
        const li = document.createElement('li');
        li.textContent = text;
        return li;
    };

    const listItemMapper = function ({address, city, distance, name}) {
        const listItem = document.createElement('li');
        const nestedList = document.createElement('ul');
        const mappedValues = {'Име': name, 'Град': city, 'Адрес': address, 'Разстояние': `${distance.toFixed(2)} км.`};

        Object
            .keys(mappedValues)
            .map(key => `${key}: ${mappedValues[key]} `)
            .map(toListItem)
            .forEach(s => nestedList.appendChild(s));

        listItem.appendChild(nestedList);
        listItem.className = 'list-item';
        return listItem;
    };

    const renderResult = function (elementToRenderIn, gasStations) {
        const fragment = document.createDocumentFragment();
        elementToRenderIn.innerHTML = '';
        gasStations.map(listItemMapper).forEach(item => fragment.appendChild(item));
        elementToRenderIn.appendChild(fragment);
    };
}(services, navigator.geolocation, google.maps.Map, google.maps.Marker));