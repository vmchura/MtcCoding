function drawPie(informales) {
    var data = [{
        values: [informales, 100-informales],
        labels: ['Informales', 'Formales'],
        'marker': {
            'colors': [
                'rgb(255, 0, 0)',
                'rgb(0, 255, 0)']},
        type: 'pie'
    }];

    var layout = {
        height: 300,
        width: 350
    };

    Plotly.newPlot('perfilCarretera', data, layout);
}