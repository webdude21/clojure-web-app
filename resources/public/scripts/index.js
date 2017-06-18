(function (services, geoLocation) {
    const getPosition = function (options) {
        return new Promise(function (resolve, reject) {
            geoLocation.getCurrentPosition(resolve, reject, options);
        });
    };

    const resultBox = document.getElementById("result-box");
    const locationBtn = document.getElementById("get-location-btn");
    locationBtn.addEventListener("click", async () => {
        let result;

        try {
            const {coords: {latitude, longitude}} = await getPosition();
            result = await services.getNearByGasStations(latitude, longitude);
        } catch (err) {
            console.warn('The user rejected to provide location');
            result = await services.getNearByGasStations();
        } finally {
            renderResult(resultBox, result);
        }
    });

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

    const renderResult = function (elementToRenderIn, {gasstations}) {
        const fragment = document.createDocumentFragment('ul');
        elementToRenderIn.innerHTML = '';
        gasstations.map(listItemMapper).forEach(item => fragment.appendChild(item));
        elementToRenderIn.appendChild(fragment);
    };
}(services, navigator.geolocation));