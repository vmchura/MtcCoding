function drawMap(xParam,yParam,targetParam){
    var data = [{
        x: xParam,
        y: yParam,
        type: 'scatter'
    }];
    var layout = {
        xaxis: {
            tickformat: ',' // For more formatting types, see: https://github.com/d3/d3-format/blob/master/README.md#locale_format
        }
    };

    Plotly.newPlot(targetParam, data, layout);

}
