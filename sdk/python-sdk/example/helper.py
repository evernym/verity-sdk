__copyright__ = "COPYRIGHT 2013-2019, ALL RIGHTS RESERVED, EVERNYM INC."
import sys
import random


def console_input(request):
    print()
    val = input(f"{request}: ")
    print()
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