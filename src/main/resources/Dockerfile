FROM ubuntu:latest as build

RUN apt-get update
RUN DEBIAN_FRONTEND=noninteractive TZ=Europe/Berlin apt-get install -y build-essential clang bison flex libreadline-dev \
                     gawk tcl-dev libffi-dev git mercurial graphviz   \
                     xdot pkg-config python2 python3 libftdi-dev gperf \
                     libboost-program-options-dev autoconf libgmp-dev \
                     cmake curl cmake ninja-build g++ python3-dev python3-setuptools \
                     python3-pip python2-dev autoconf gperf
RUN curl https://bootstrap.pypa.io/pip/2.7/get-pip.py --output get-pip.py
RUN /usr/bin/python2.7 get-pip.py

WORKDIR /home/yosys
RUN mkdir tools
WORKDIR /home/yosys/tools

RUN git clone https://github.com/YosysHQ/yosys.git yosys
WORKDIR /home/yosys/tools/yosys
RUN mkdir build
WORKDIR /home/yosys/tools/yosys/build
RUN make -j$(nproc) -f ../Makefile
RUN make install -f ../Makefile
WORKDIR /home/yosys/tools

RUN git clone https://github.com/YosysHQ/SymbiYosys.git SymbiYosys
WORKDIR /home/yosys/tools/SymbiYosys
RUN make -j$(nproc)
RUN make install
WORKDIR /home/yosys/tools

RUN git clone https://github.com/SRI-CSL/yices2.git yices2
WORKDIR /home/yosys/tools/yices2
RUN autoconf
RUN ./configure
RUN make -j$(nproc)
RUN make install
WORKDIR /home/yosys/tools

RUN git clone https://github.com/Z3Prover/z3.git z3
WORKDIR /home/yosys/tools/z3
RUN git fetch --all --tags
RUN git checkout tags/z3-4.11.2 -b z3-4.11.2-branch
RUN python3 scripts/mk_make.py
WORKDIR /home/yosys/tools/z3/build
RUN make -j$(nproc)
RUN make install
WORKDIR /home/yosys/tools

RUN git clone --recursive https://github.com/sterin/super-prove-build
WORKDIR /home/yosys/tools/super-prove-build
RUN mkdir build
WORKDIR /home/yosys/tools/super-prove-build/build
RUN cmake -DCMAKE_BUILD_TYPE=Release -G Ninja ..
RUN ninja
RUn sed -i 's/"--system" "--target"/"--target"/' cmake_install.cmake
RUN ninja package
RUN tar -C /usr/local -xf super_prove*.tar.gz
RUN touch /usr/local/bin/suprove
RUN echo "#!/bin/bash" > /usr/local/bin/suprove
RUN echo "tool=super_prove; if [ \"$$1\" != \"$${1#+}\" ]; then tool=\"${1#+}\"; shift; fi" >> /usr/local/bin/suprove
RUN echo "exec /usr/local/super_prove/bin/$${tool}.sh \"$$@\"" >> /usr/local/bin/suprove
RUN chmod +x /usr/local/bin/suprove
WORKDIR /home/yosys/tools

RUN git clone https://bitbucket.org/arieg/extavy.git
WORKDIR /home/yosys/tools/extavy
RUN git submodule update --init
RUN sed -i 's/bool isSolved () { return m_Trivial || m_State || !m_State; }/ bool isSolved () { return bool{m_Trivial || m_State || !m_State}; }/' avy/src/ItpGlucose.h
RUN sed -i 's/return tobool (m_pSat->modelValue(x));/boost::logic::tribool y = tobool (m_pSat->modelValue(x));\n        return bool{y};/' avy/src/ItpGlucose.h
RUN sed -i 's/bool isSolved () { return m_Trivial || m_State || !m_State; }/bool isSolved () { return bool{m_Trivial || m_State || !m_State}; }/' avy/src/ItpMinisat.h
RUN mkdir build
WORKDIR /home/yosys/tools/extavy/build
RUN cmake -DCMAKE_BUILD_TYPE=Release ..
RUN make -j$(nproc)
RUN cp avy/src/avy /usr/local/bin/
RUN cp avy/src/avybmc /usr/local/bin/
WORKDIR /home/yosys/tools

RUN git clone https://github.com/boolector/boolector
WORKDIR /home/yosys/tools/boolector
RUN ./contrib/setup-btor2tools.sh
RUN ./contrib/setup-lingeling.sh
RUN ./configure.sh
RUN make -C build -j$(nproc)
RUN cp build/bin/boolector /usr/local/bin/
RUN cp build/bin/btor* /usr/local/bin/
RUN cp deps/btor2tools/bin/btorsim /usr/local/bin/
WORKDIR /home/yosys/tools

RUN git clone https://github.com/boolector/btor2tools
WORKDIR /home/yosys/tools/btor2tools
RUN ./configure.sh
RUN cmake . -DBUILD_SHARED_LIBS=OFF
RUN make -j$(nproc)
RUN make install
WORKDIR /home/yosys/tools

RUN curl -sSL https://get.haskellstack.org/ | sh
RUN git clone https://github.com/zachjs/sv2v.git
WORKDIR /home/yosys/tools/sv2v
RUN make -j$(nproc)
RUN stack install
WORKDIR /home/yosys/tools

RUN git clone https://github.com/steveicarus/iverilog.git
WORKDIR /home/yosys/tools/iverilog
RUN sh autoconf.sh
RUN ./configure
RUN make -j$(nproc)
RUN make install
WORKDIR /home/yosys/tools

WORKDIR /home/yosys

FROM ubuntu:latest as main

RUN groupadd -g 1001 yosys
RUN useradd yosys -u 1001 -g 1001 -m -d /home/yosys -s /bin/bash
WORKDIR /home/yosys

RUN apt update
RUN apt upgrade -y
RUN DEBIAN_FRONTEND=noninteractive TZ=Europe/Berlin apt install -y \
        nano git bison flex libreadline-dev \
        libffi-dev graphviz \
        xdot python3 libftdi-dev gperf \
        libboost-program-options-dev libgmp-dev \
        curl python3-pip openjdk-18-jre-headless openjdk-18-dbg maven \
        tcl-dev python2.7-dev

COPY --from=build /usr/local/bin /usr/local/bin
COPY --from=build /usr/local/lib /usr/local/lib
COPY --from=build /usr/local/include /usr/local/include
COPY --from=build /usr/local/share /usr/local/share
COPY --from=build /usr/bin/z3 /usr/bin/z3
COPY --from=build /usr/local/super_prove /usr/local/super_prove
COPY --from=build /root/.local/bin/sv2v /usr/local/bin/sv2v

USER yosys

CMD ["/bin/bash"]