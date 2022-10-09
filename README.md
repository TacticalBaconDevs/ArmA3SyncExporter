# ArmA3SyncExporter

## Requirements
- Arma3Sync
    - Site/Wiki: http://www.sonsofexiled.fr/wiki/index.php/ArmA3Sync_Wiki_English
    - SVN: svn://www.sonsofexiled.fr/repository/ArmA3Sync/releases
- Java 8
    - https://adoptium.net/temurin/releases/?version=8

## Informations
This project extracts/exports data from an Arma3Sync repo.

## Example call and output
### Input:
java -jar ArmA3SyncExporter.jar -console http://repo.tacticalbacon.de/.a3s/events

### Output:
```
v1.0.1 (09.10.22)
-----------------START-----------------
[TB] MinimalModset#@TBMod|@Zeus Enhanced|@LAMBS_Danger|@diwako_dui|@CBA_A3|@ace
[TB] TestServer#@TBMod|@CBA_A3|@KAT_AdvancedMedical|@ace|@BuildingMods
-----------------END-----------------
```

## Current help
```
java -jar ArmA3SyncExporter.jar [options] <URL>
Options:
	-h / -? / -help: shows help
	-console: output option as console output
URL:
	http://***/sync => ADDON|FILE1|FILE2|...
	http://***/events => EVENT|ADDON1|ADDON2|...
```    
