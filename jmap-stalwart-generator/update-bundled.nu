#!/usr/bin/env nu

const pom_file = path self pom.xml
const schema_file = path self src/main/resources/schema.json

def main [] {
    let pom = open --raw $pom_file
    let bundled_version = $pom
    | parse --regex '<schema.version>(.*?)</schema.version>'
    | get capture0

    let tag = http get -H {
        Accept: "application/vnd.github+json",
        X-GitHub-Api-Version: "2026-03-10",
    } "https://api.github.com/repos/stalwartlabs/stalwart/releases/latest"
    | get tag_name
    let version = $tag | str trim --left -c 'v'

    if $bundled_version != $version {
        http get $"https://github.com/stalwartlabs/stalwart/raw/refs/tags/($tag)/resources/schema/schema.json.gz"
        | gunzip
        | save -f $schema_file

        $pom
        | str replace -r '(<schema.version>).*?(</schema.version>)' $'${1}($version)${2}'
        | save -f $pom_file
    }
}
