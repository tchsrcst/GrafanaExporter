# GrafanaExporter

Utility program for saving batches of chart .PNG images from specific [Grafana](https://grafana.com/grafana/) Dashboard with configured [Grafana Image Renderer](https://grafana.com/grafana/plugins/grafana-image-renderer/) plugin.

> [RU] 
> Утилита для сохранения PNG изображений графиков с определеннного дашборда [Grafana](https://grafana.com/grafana/) с настроенным плагином [Grafana Image Renderer](https://grafana.com/grafana/plugins/grafana-image-renderer/). 

### Command Line Interface Usage 

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

## Run Examples

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

### XML config

#### 'Configuration' XMLElement

http://localhost/grafana/d/5FGhJhv4z/sample-sashboard?orgId=1
http://localhost/grafana/d/5FGhJhv4z/sample-sashboard?viewPanel=1&orgId=1

| Option            | Type    | Description                                                                                                     |
|-------------------|---------|-----------------------------------------------------------------------------------------------------------------|
| Host    | String  | Grafana base URL. <br/> Example:  http://localhost:3000                                                         |
| Dashboard     | String  | Dashboard name. <br/> Example: <span>http://</span>localhost/grafana/d/5FGhJhv4z/***sample-sashboard***?orgId=1 |
| DashboardUID    | String  | Dashboard UID <br/> Example: <span>http://</span>localhost/grafana/d/***5FGhJhv4z***/sample-sashboard?orgId=1   |
| ApiKey | String  | Grafana API key. See [documentation](https://grafana.com/docs/grafana/latest/administration/api-keys/).         |
| Destination     | String  | Destination directory to save images. Example: `C:\out`                                                                         |
| Timezone       | String  | Timezone. Example: `Europe/Moscow`                                                                                |
| Timeout       | Integer | Timeout for downloads. Example: `120`                                                                             |
| Graph       | Array   | Array of 'Graph' XMLElement                                                                                     |

#### 'Graph' XMLElement

| Option            | Type    | Description                                                                                                                        |
|-------------------|---------|------------------------------------------------------------------------------------------------------------------------------------|
| PanedId      | String  | Id of dashboard panel. <br/> Example: <span>http://</span>localhost/grafana/d/5FGhJhv4z/sample-sashboard?viewPanel=***1***&orgId=1 |
| Name    | String  | Name for chart image file. Example: `cpu_usage_myhost`                                                                             |
| Folder     | String  | Subfolder to save image to. Example: `myhost`                                                                                      |
| Width    | Integer | Image width in pixes. Example: `1000`                                                                                              |
| Height | Integer | Image height in pixes. Example: `500`                                                                                              |
| Var     | Array   | Array of Grafana variables to pass in `key=value` format. Examples: `theme=light`, `host=myhost`                                   |

#### XSD

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
                            <xs:element name="PanedId" type="xs:unsignedByte" />
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

#### Sample XML config

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
