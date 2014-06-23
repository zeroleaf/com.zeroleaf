#!/bin/bash

filepath=$(cd $(dirname "$0"); pwd)
secv=${filepath}"/com/zeroleaf/secv/Secv.groovy"

groovy $secv
