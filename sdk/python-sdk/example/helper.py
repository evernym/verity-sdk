__copyright__ = "COPYRIGHT 2013-2019, ALL RIGHTS RESERVED, EVERNYM INC."

import json
import sys
import random
from halo import Halo


def print_message(msg_name, message):
    print_object(message, "<<<", f"Incoming Message -- {msg_name}")


def print_object(obj, prefix, preamble):
    print(f"{prefix}  {preamble}")

    if isinstance(obj, str):
        obj_str = obj
    else:
        obj_str = json.dumps(obj, indent=2)

    for line in obj_str.splitlines():
        print(f"{prefix} {line}")

    print()


def non_handled(msg: str, message=None):
    global server
    print_error(msg)
    if message is not None:
        print_error(message)
    server.close()
    sys.exit(1)


def load_context(file_path) -> str:
    try:
        with open(file_path, 'r') as f:
            if console_yes_no(f"Reuse Verity Context (in {file_path})", True):
                return f.read()
    except FileNotFoundError:
        pass
    return ""


def default_handler(_, message):
    print("Message not handled!")
    print(message)


def make_spinner(msg):
    return Halo(text=f'{msg} ... ', spinner='line', interval=450, color=None, placement='right')


def console_input(request):
    print()
    val = input(f"{request}: ")
    if not val:
        return ""
    return val


def console_yes_no(request, default_yes):
    yes_no = "[y]/n" if default_yes else "y/n"
    modified_request = request + "? " + yes_no
    response = console_input(modified_request).strip().lower()

    if default_yes and not response:
        return True
    elif "y" == response:
        return True
    elif "n" == response:
        return False
    raise Exception("Did not get a valid response -- '" + response + "' is not y or n")


def print_error(*args, **kwargs):
    print(*args, file=sys.stderr, **kwargs)


def get_random_version():
    return '{}.{}.{}'.format(get_random_int(), get_random_int(), get_random_int())


def get_random_int():
    random.seed()
    return random.randrange(0, 1000)