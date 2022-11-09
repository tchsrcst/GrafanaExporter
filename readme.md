# GrafanaExporter

+ [Introduction](#introduction)
+ [Command Line Interface](#cli)
+ [XML config](#xml-config)

## Introduction

GrafanaExporter is an utility program for saving batches of chart .PNG images from specific [Grafana](https://grafana.com/grafana/) dashboard with configured [grafana-image-renderer](https://grafana.com/grafana/plugins/grafana-image-renderer/) plugin.

> [RU] 
> Утилита для сохранения PNG изображений графиков с определеннного дашборда [Grafana](https://grafana.com/grafana/) с настроенным плагином [Grafana Image Renderer](https://grafana.com/grafana/plugins/grafana-image-renderer/). 

GrafanaExporter uses `grafana-image-renderer`-provided HTTP API interface to download and save chart images.

Chart download URL example for Grafana with `grafana-image-renderer`:

```
http://grafana_host:3000/render/d-solo/PS4sLaC7z/sample-dashboard?panelId=1&from=1667380000000&to=1667393000000&var-host=myhost&theme=light&width=1000&height=500&tz=Europe%2FMoscow&timeout=120
```

## CLI

Usage:
```text
GrafanaExporter.jar [options]
```

| Option            | Type          | Description                                                                                         |
|-------------------|---------------|-----------------------------------------------------------------------------------------------------|
| --config, -c      | Required | Path to GrafanaExporter XML configuration file                                                      |
| --from, -f        | Required      | Value to use as time range 'from' part for charts                                                   |
| --to, -t          | Required     | Value to use as time range 'to' part for charts                                                     |
| --concurrency     | Optional     | Number of concurrent downloads. Default: 1                                                          |
| --destination, -d | Optional      | Destination path for saving images files. Overrides destination specified in XML configuration file |
| --prefix, -p      | Optional     | Prefix to add to all file names specified in XML configuration file                                 |
| --help, -h        | Optional      | Show usage help                                                                                     |

### Run Examples

Relative time:
```shell
    java -jar GrafanaExporter-{version}.jar --config C:\config.xml -f now-1h -t now
````

Epoch millis:
```shell
    java -jar GrafanaExporter-{version}.jar --config C:\config.xml -f 1655308790000 -t 16553087960000
````

YYYY-MM-DD'T'HH24:MI:SS format:
```shell
    java -jar GrafanaExporter-{version}.jar --config C:\config.xml -f 2020-01-01T10:00:00 -t 2020-01-01T11:00:00
````

## XML config

### Config example

```xml
<Configuration>
    <Host>http://myhost:3000</Host>
    <Dashboard>sample-sashboard</Dashboard>
    <DashboardUID>SSVf6YRWk</DashboardUID>
    <ApiKey>AQ==</ApiKey>
    <Destination>C:\out</Destination>
    <Timezone>Europe/Moscow</Timezone>
    <Timeout>120</Timeout>
    <Graph>
        <PanedId>1</PanedId>
        <Name>cpu_usage</Name>
        <Folder>myhost</Folder>
        <Width>1000</Width>
        <Height>500</Height>
        <Var>host=myhost</Var>
        <Var>theme=light</Var>
    </Graph>
    <Graph>
        <PanedId>2</PanedId>
        <Name>mem_usage</Name>
        <Folder>myhost</Folder>
        <Width>1000</Width>
        <Height>500</Height>
        <Var>host=myhost</Var>
        <Var>theme=light</Var>
    </Graph>
</Configuration>
```

### Root 'Configuration' XMLElement

| Option       | Type                        | # of Occurence | Description                                                                                                          |
|--------------|-----------------------------|----------------|----------------------------------------------------------------------------------------------------------------------|
| Host         | String                      | {1}            | Grafana base URL. <br/> Example:  http://localhost:3000                                                              |
| Dashboard    | String                      | {1}            | Dashboard name. <br/> Example: <span>http://</span>localhost/grafana/d/5FGhJhv4z/***sample-sashboard***?orgId=1      |
| DashboardUID | String                      | {1}            | Dashboard unique ID. <br/> Example: <span>http://</span>localhost/grafana/d/***5FGhJhv4z***/sample-sashboard?orgId=1 |
| ApiKey       | String                      | {1}            | Grafana API key. See [documentation](https://grafana.com/docs/grafana/latest/administration/api-keys/)               |
| Destination  | String                      | {1}            | Destination directory to save images. Example: `C:\out`                                                              |
| Timezone     | String                      | {0, 1}         | Timezone. Example: `Europe/Moscow`                                                                                   |
| Timeout      | Integer                     | {0, 1}         | Timeout for downloads. Example: `120`                                                                                |
| Graph        | Array of 'Graph' XMLElement | {0,}           | List of panel specific information.                                                                                  |

### Child 'Graph' XMLElement

| Option   | Type    | # of Occurence |Description |
|----------|---------|----------------|-----|
| PanedId  | String  | {1}            | Id of dashboard panel. <br/> Example: <span>http://</span>localhost/grafana/d/5FGhJhv4z/sample-sashboard?viewPanel=***1***&orgId=1 |
| Name     | String  | {1}            | Name for chart image file. Example: `cpu_usage_myhost`|
| Folder   | String  | {0,1}          | Subfolder to save image to. Example: `myhost`|
| Width    | Integer | {1}            | Image width in pixes. Example: `1000` |
| Height   | Integer | {1}            | Image height in pixes. Example: `500` |
| Var      | Array   | {0,}           | Array of Grafana variables to pass in `key=value` format. Examples: `theme=light`, `host=myhost`|

### XMLSchema

<details>
        <summary>XMLSchema</summary>
```xml
<?xml version="1.0" encoding="utf-8"?>
<!-- Created with Liquid Technologies Online Tools 1.0 (https://www.liquid-technologies.com) -->
<xs:schema attributeFormDefault="unqualified" elementFormDefault="qualified" xmlns:xs="http://www.w3.org/2001/XMLSchema">
    <xs:element name="Configuration">
        <xs:complexType>
            <xs:sequence>
                <xs:element name="Host" type="xs:string" />
                <xs:element name="Dashboard" type="xs:string" />
                <xs:element name="DashboardUID" type="xs:string" />
                <xs:element name="ApiKey" type="xs:string" />
                <xs:element name="Destination" type="xs:string" />
                <xs:element name="Timezone" type="xs:string" />
                <xs:element name="Timeout" type="xs:unsignedShort" />
                <xs:element maxOccurs="unbounded" name="Graph">
                    <xs:complexType>
                        <xs:sequence>
                            <xs:element name="PanedId" type="xs:unsignedShort" />
                            <xs:element name="Name" type="xs:string" />
                            <xs:element name="Folder" type="xs:string" />
                            <xs:element name="Width" type="xs:unsignedShort" />
                            <xs:element name="Height" type="xs:unsignedShort" />
                            <xs:element maxOccurs="unbounded" name="Var" type="xs:string" />
                        </xs:sequence>
                    </xs:complexType>
                </xs:element>
            </xs:sequence>
        </xs:complexType>
    </xs:element>
</xs:schema>
```
</details>