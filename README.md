# Explode 3 - CParser 谱面解析

A tool for parsing and transforming chart map.

## How to Use

### Analyze

```bash
java -jar cm.jar analyze -i <path-to-xml>
```

### Transform

```bash
java -jar cm.jar transform <operation> [...args] -i <path-to-xml-input> -o <path-to-xml-output>
```

#### Op: width

Changes the width of notes.

```bash
java -jar cm.jar transform width <ratio: double> -i <path-to-xml-input> -o <path-to-xml-output>
```

#### Op: mirror

```bash
java -jar cm.jar transform mirror -i <path-to-xml-input> -o <path-to-xml-output>
```