function drawMap(xParam,y0Param,y1Param,targetParam){
    trace1 = {
        type: 'scatter',
        x: xParam,
        y: y0Param,
        mode: 'lines',
        name: 'Promedio',
        line: {
            color: 'rgb(219, 64, 82)',
            width: 3
        }
    };

    trace2 = {
        type: 'scatter',
        x: xParam,
        y: y1Param,
        mode: 'lines',
        name: 'Limite',
        line: {
            color: 'rgb(55, 128, 191)',
            width: 1
        }
    };
    var data = [trace1, trace2];

    var layout = {
        xaxis: {
            tickformat: ',' // For more formatting types, see: https://github.com/d3/d3-format/blob/master/README.md#locale_format
        },
        title: 'Comparación - Velocidad Promedio y Límites de velocidad',

    };

    Plotly.newPlot(targetParam, data, layout);

}
