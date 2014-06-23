#!/bin/bash

filepath=$(cd $(dirname "$0"); pwd)
secv=${filepath}"/src/com/zeroleaf/secv/Secv.groovy"

groovy $secv
