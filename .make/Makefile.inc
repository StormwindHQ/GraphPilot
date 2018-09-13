TIME		:= $(shell date +"%Y-%m-%d_%H%M%S")
SELF_DIR    := $(dir $(lastword $(MAKEFILE_LIST)))
export

include $(SELF_DIR)/includes/colours.mk
include $(SELF_DIR)/includes/help.mk

