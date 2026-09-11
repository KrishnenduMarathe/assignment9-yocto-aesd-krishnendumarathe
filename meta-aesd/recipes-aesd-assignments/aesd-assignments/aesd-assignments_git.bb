inherit update-rc.d module

INITSCRIPT_NAME = "aesdinit"
INITSCRIPT_PARAMS = "defaults 99"

# See https://git.yoctoproject.org/poky/tree/meta/files/common-licenses
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# TODO: Set this  with the path to your assignments rep.  Use ssh protocol and see lecture notes
# about how to setup ssh-agent for passwordless access
SRC_URI = "git://git@github.com/KrishnenduMarathe/assignment3-and-later-aesd-krishnendumarathe;protocol=ssh;branch=main file://S97aesdcharmodules file://custom_init"

PV = "1.0+git${SRCPV}"

# TODO: set to reference a specific commit hash in your assignment repo
SRCREV = "8eec1e056a1b8d6bf3b9b38312e2527d2491216a"

# This sets your staging directory based on WORKDIR, where WORKDIR is defined at 
# https://docs.yoctoproject.org/ref-manual/variables.html?highlight=workdir#term-WORKDIR
# We reference the "server" directory here to build from the "server" directory
# in your assignments repo
S = "${WORKDIR}/git"

# TODO: Add the aesdsocket application and any other files you need to install
# See https://git.yoctoproject.org/poky/plain/meta/conf/bitbake.conf?h=kirkstone
FILES:${PN} += "${bindir}/aesdsocket \
                ${sysconfdir}/init.d/aesdsocket \
                ${sysconfdir}/init.d/aesdchar \
                ${sysconfdir}/init.d/aesdinit"

# TODO: customize these as necessary for any libraries you need for your application
# (and remove comment)
TARGET_LDFLAGS += "-lpthread -lrt"

# Requirements for running in yocto image
RDEPENDS:${PN} += "libgcc"

# yocoto pass kernel build directory
EXTRA_OEMAKE += "KERNELDIR=${STAGING_KERNEL_DIR}"

#KERNEL_MODULE_AUTOLOAD += "aesdchar"

do_configure () {
	:
}

do_compile () {
	oe_runmake -C server
	oe_runmake -C aesd-char-driver
}

do_install () {
	# TODO: Install your binaries/scripts here.
	# Be sure to install the target directory with install -d first
	# Yocto variables ${D} and ${S} are useful here, which you can read about at 
	# https://docs.yoctoproject.org/ref-manual/variables.html?highlight=workdir#term-D
	# and
	# https://docs.yoctoproject.org/ref-manual/variables.html?highlight=workdir#term-S
	# See example at https://github.com/cu-ecen-aeld/ecen5013-yocto/blob/ecen5013-hello-world/meta-ecen5013/recipes-ecen5013/ecen5013-hello-world/ecen5013-hello-world_git.bb

	install -d ${D}${bindir}
	install -m 0755 ${S}/server/aesdsocket ${D}${bindir}/
	
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${S}/server/aesdsocket-start-stop.sh ${D}${sysconfdir}/init.d/aesdsocket

    install -d ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra
    install -m 0644 ${S}/aesd-char-driver/aesdchar.ko ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/

    install -m 0755 ${WORKDIR}/S97aesdcharmodules ${D}${sysconfdir}/init.d/aesdchar

    install -m 0755 ${WORKDIR}/custom_init ${D}${sysconfdir}/init.d/aesdinit
}
