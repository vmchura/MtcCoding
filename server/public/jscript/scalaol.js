function createMap(target){
    var TileLayer = ol.layer.Tile;
    var openCycleMapLayer = new TileLayer({
        source: new ol.source.OSM()
    });



    var map = new ol.Map({
        layers: [
            openCycleMapLayer
        ],
        target: "demoMap",
        view: new ol.View({
            maxZoom: 18,
            center: [-8486558.521727294, -1111420.8471308986 ],
            zoom: 15
        })
    });

    return map;

}

function addRoute(map){

    var c0 = [ -8486558.521727294, -1111420.8471308986 ];
    var c1 = [ -8610185.27054937, -956184.9863940852 ];
    var c2 = [ -8443008.18975588, -951268.0134295707 ];


    var route = new ol.geom.LineString([c0,c1,c2]);
    var routeFeature = new ol.Feature({
        type: 'route',
        geometry: route
    });
    var vectorLayer = new ol.layer.Vector({
        source: new ol.source.Vector({
            features: [routeFeature]
        }),
        style: new ol.style.Style({
            stroke: new ol.style.Stroke({
                width: 6, color: [237, 212, 0, 0.8]
            })
        })
    });

    map.addLayer(vectorLayer);
}
function addRouteFromRoute(map,route){


    var routeFeature = new ol.Feature({
        type: 'route',
        geometry: route
    });
    var vectorLayer = new ol.layer.Vector({
        source: new ol.source.Vector({
            features: [routeFeature]
        }),
        style: new ol.style.Style({
            stroke: new ol.style.Stroke({
                width: 6, color: [237, 212, 0, 0.8]
            })
        })
    });

    map.addLayer(vectorLayer);
}

function addRouteFromFeauture(map,routeFeature){


    var vectorLayer = new ol.layer.Vector({
        source: new ol.source.Vector({
            features: [routeFeature]
        }),
        style: new ol.style.Style({
            stroke: new ol.style.Stroke({
                width: 6, color: [237, 212, 0, 0.8]
            })
        })
    });

    map.addLayer(vectorLayer);
}