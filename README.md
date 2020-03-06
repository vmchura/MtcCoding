# MTC-CODING


mtc_pasajero
------------

data_type |character_maximum_length|
----------|------------------------|
idPasajero|                        |


mtc_ruta
------------

column_name|data_type        |character_maximum_length|
-----------|-----------------|------------------------|
idRuta     |integer          |                        |
tagRuta    |character varying|                      32|
progFin    |integer          |                        |


mtc_limitevelocidad
------------

data_type|character_maximum_length|
---------|------------------------|
idLimite |                        |
idRuta   |                        |
progIni  |                        |
progFin  |                        |
limite   |                        |

mtc_travel
------------

data_type |character_maximum_length|
----------|------------------------|
idTravel  |                        |
startTime |                        |
placa     |                      32|
idRuta    |                        |
isLive    |                        |
direction |                        |
idPasajero|                        |
infringio |                        |





mtc_datatravel
------------


data_type  |character_maximum_length|
-----------|------------------------|
idDataTable|                        |
dataTime   |                        |
idTravel   |                        |
prog       |                        |
velocidad  |                        |