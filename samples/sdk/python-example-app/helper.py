__copyright__ = "COPYRIGHT 2013-2019, ALL RIGHTS RESERVED, EVERNYM INC."

import json
import random
import sys
from halo import Halo

ANSII_GREEN = '\u001b[32m'
ANSII_RESET = '\x1b[0m'

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


def non_handled(error_msg: str, received_message=None):
    global server
    print_error(error_msg)
    if received_message is not None:
        print_error(received_message)
    if server:
        server.close()
    sys.exit(1)


def default_handler(message):
    non_handled(f'Message name is not handled', message)


def make_spinner(msg):
    return Halo(text=f'{msg} ... ', spinner='line', interval=450, color=None, placement='right')


def console_input(request, default_value=None):
    print()
    if default_value:
        print(f'{request}:')
        print(f'{ANSII_GREEN}{default_value}{ANSII_RESET} is set via environment variable')
        input('Press any key to continue')
        return default_value
    else:
        val = input(f"{request}: ").strip()
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


async def noop(msg_name, message):
    pass
