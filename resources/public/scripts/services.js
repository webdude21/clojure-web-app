const services = (function (fetch, URL, BASE_URL) {

    const prepareQuery = function (url, params) {
        const urlObject = new URL(url);
        Object.keys(params)
            .filter(x => !!params[x])
            .forEach(key => urlObject.searchParams.append(key, params[key]));
        return urlObject;
    };

    return {
        async getNearByGasStations(lat, lon, limit = 10, distance = 10, fuel = 'lpg'){
            const query = prepareQuery(`${BASE_URL}/rest/fuel-near-me`, {lat, lon, limit, distance, fuel});
            const rawResult = await fetch(query);
            return await rawResult.json();
        }
    }
}(window.fetch, window.URL, window.location.origin));